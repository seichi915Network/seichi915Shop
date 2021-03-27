package net.seichi915.seichi915shop.listener

import cats.effect.IO
import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldguard.protection.flags.Flags
import net.seichi915.seichi915shop.Seichi915Shop
import net.seichi915.seichi915shop.data.store.StoreData
import net.seichi915.seichi915shop.database.Database
import net.seichi915.seichi915shop.external.ExternalPlugins
import net.seichi915.seichi915shop.menu.StoreMenu
import net.seichi915.seichi915shop.util.Implicits._
import org.bukkit.ChatColor
import org.bukkit.block.Sign
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.{EventHandler, Listener}

import java.util.UUID
import scala.concurrent.ExecutionContext

class PlayerInteractListener extends Listener {
  @EventHandler
  def onPlayerInteract(event: PlayerInteractEvent): Unit = {
    if (event.getAction != Action.RIGHT_CLICK_BLOCK) return
    if (!event.getClickedBlock.getState.isInstanceOf[Sign]) return
    if (Seichi915Shop.storeCreationMap.contains(event.getPlayer)) {
      val shop = Seichi915Shop.storeCreationMap(event.getPlayer)
      if (Seichi915Shop.storeDataList
            .map(_.getLocation)
            .contains(event.getClickedBlock.getLocation)) {
        event.getPlayer.sendMessage("既に店舗が存在します。".toErrorMessage)
        return
      }
      if (!event.getPlayer.isOp) {
        ExternalPlugins.getWorldGuardPlugin match {
          case Some(worldGuardPlugin) =>
            if (!event.getPlayer.hasPermission(
                  s"worldguard.region.bypass.${event.getClickedBlock.getLocation.getWorld.getName}")) {
              val worldGuard = ExternalPlugins.getWorldGuard.get
              val regionQuery =
                worldGuard.getPlatform.getRegionContainer.createQuery
              val localPlayer = worldGuardPlugin.wrapPlayer(event.getPlayer)
              val canBlockPlace = regionQuery.testState(
                BukkitAdapter.adapt(event.getClickedBlock.getLocation),
                localPlayer,
                Flags.BLOCK_PLACE)
              val canBlockBreak = regionQuery.testState(
                BukkitAdapter.adapt(event.getClickedBlock.getLocation),
                localPlayer,
                Flags.BLOCK_BREAK)
              if (!canBlockPlace || !canBlockBreak) {
                event.getPlayer.sendMessage(
                  "看板の編集がブロックされました。保護を確認してください。".toErrorMessage)
                return
              }
            }
          case None =>
        }
        ExternalPlugins.getGriefPrevention match {
          case Some(griefPrevention) =>
            val allowBuild = griefPrevention.allowBuild(
              event.getPlayer,
              event.getClickedBlock.getLocation)
            if (allowBuild.nonNull && allowBuild != "") {
              event.getPlayer.sendMessage(
                "看板の編集がブロックされました。保護を確認してください。".toErrorMessage)
              return
            }
          case None =>
        }
      }
      val storeData = StoreData(event.getClickedBlock.getLocation,
                                UUID.randomUUID(),
                                shop.getUUID)
      Seichi915Shop.storeDataList =
        Seichi915Shop.storeDataList.appended(storeData)
      val sign = event.getClickedBlock.getState.asInstanceOf[Sign]
      sign.setLine(0, s"${ChatColor.GREEN}seichi915Shop")
      sign.setLine(1, shop.getName)
      sign.setLine(3, "右クリックで開けます。")
      sign.update()
      Seichi915Shop.storeCreationMap.remove(event.getPlayer)
      event.getPlayer.sendMessage("店舗を作成しました。".toSuccessMessage)
    } else if (Seichi915Shop.storeDeletionList.contains(event.getPlayer)) {
      if (!Seichi915Shop.storeDataList
            .map(_.getLocation)
            .contains(event.getClickedBlock.getLocation)) {
        event.getPlayer.sendMessage("その看板は店舗として設定されていません。".toErrorMessage)
        return
      }
      val storeData = Seichi915Shop.storeDataList
        .map(storeData => storeData.getLocation -> storeData)
        .toMap
        .apply(event.getClickedBlock.getLocation)
      val shopData = Seichi915Shop.shopDataList
        .map(shopData => shopData.getUUID -> shopData)
        .toMap
        .apply(storeData.getShop)
      if (shopData.getOwner != event.getPlayer.getUniqueId) {
        event.getPlayer.sendMessage("所有権がありません。".toErrorMessage)
        return
      }
      Seichi915Shop.storeDataList =
        Seichi915Shop.storeDataList.filter(_.getUUID != storeData.getUUID)
      val sign = event.getClickedBlock.getState.asInstanceOf[Sign]
      sign.setLine(0, "")
      sign.setLine(1, "")
      sign.setLine(2, "")
      sign.setLine(3, "")
      sign.update()
      Seichi915Shop.storeDeletionList = Seichi915Shop.storeDeletionList.filter(
        _.getUniqueId != event.getPlayer.getUniqueId)
      val task = IO {
        Database.deleteStoreData(storeData)
        event.getPlayer.sendMessage("店舗の削除が完了しました。".toSuccessMessage)
      }
      val contextShift = IO.contextShift(ExecutionContext.global)
      IO.shift(contextShift).flatMap(_ => task).unsafeRunAsyncAndForget()
    } else {
      if (!Seichi915Shop.storeDataList
            .map(_.getLocation)
            .contains(event.getClickedBlock.getLocation)) return
      val storeData = Seichi915Shop.storeDataList
        .map(storeData => storeData.getLocation -> storeData)
        .toMap
        .apply(event.getClickedBlock.getLocation)
      val shopData = Seichi915Shop.shopDataList
        .map(shopData => shopData.getUUID -> shopData)
        .toMap
        .getOrElse(
          storeData.getShop, {
            event.getPlayer.sendMessage(
              s"ショップ ${storeData.getUUID} が見つかりませんでした。".toErrorMessage)
            Seichi915Shop.storeDataToDeleteList =
              Seichi915Shop.storeDataToDeleteList.appended(storeData)
            Seichi915Shop.storeDataList =
              Seichi915Shop.storeDataList.filter(_.getUUID != storeData.getUUID)
            val sign = event.getClickedBlock.getState.asInstanceOf[Sign]
            sign.setLine(0, "")
            sign.setLine(1, "")
            sign.setLine(2, "")
            sign.setLine(3, "")
            sign.update()
            return
          }
        )
      StoreMenu(shopData).open(event.getPlayer)
    }
  }
}
