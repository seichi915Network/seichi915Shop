package net.seichi915.seichi915shop.listener

import net.seichi915.seichi915shop.Seichi915Shop
import net.seichi915.seichi915shop.util.Implicits._
import org.bukkit.block.Sign
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.{EventHandler, Listener}

class BlockBreakListener extends Listener {
  @EventHandler
  def onBlockBreak(event: BlockBreakEvent): Unit = {
    if (!event.getBlock.getState.isInstanceOf[Sign]) return
    if (Seichi915Shop.storeDataList
          .map(_.getLocation)
          .contains(event.getBlock.getLocation)) {
      event.getPlayer.sendMessage("その看板は店舗として設定されています。".toErrorMessage)
      event.getPlayer.sendMessage(
        "店舗の削除は /deletestore コマンドを使用してください。".toErrorMessage)
      event.setCancelled(true)
    }
  }
}
