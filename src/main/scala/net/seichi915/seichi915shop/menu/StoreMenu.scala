package net.seichi915.seichi915shop.menu

import cats.effect.IO
import net.seichi915.seichi915shop.Seichi915Shop
import net.seichi915.seichi915shop.builder.ItemStackBuilder
import net.seichi915.seichi915shop.data.shop.ShopData
import net.seichi915.seichi915shop.external.ExternalPlugins
import net.seichi915.seichi915shop.inventory.Seichi915ShopInventoryHolder
import net.seichi915.seichi915shop.meta.menu.Menu
import net.seichi915.seichi915shop.util.Implicits._
import org.bukkit.{Bukkit, ChatColor, Material}
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag

import scala.concurrent.ExecutionContext
import scala.jdk.CollectionConverters._

case class StoreMenu(shopData: ShopData) extends Menu {
  override def open(player: Player): Unit = {
    val inventory = Bukkit.createInventory(
      Seichi915ShopInventoryHolder.seichi915ShopInventoryHolder,
      45,
      ChatColor.translateAlternateColorCodes('&', shopData.getDisplayName))
    val closeButton = ItemStackBuilder(Material.BARRIER)
      .setDisplayName(s"${ChatColor.RED}閉じる")
      .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
      .build
    closeButton.setClickAction(_.closeInventory())
    inventory.setItem(44, closeButton)
    Seichi915Shop.productDataList
      .filterNot(_.getShop != shopData.getUUID)
      .foreach { productData =>
        val itemStack = productData.getItem.clone()
        val itemMeta = itemStack.getItemMeta
        val newLore = List(
          "",
          s"${ChatColor.GREEN}価格: ${ChatColor.WHITE}${productData.getPrice}円",
          s"${if (productData.hasEnoughStock) s"${ChatColor.GREEN}在庫あり"
          else s"${ChatColor.RED}在庫なし"}",
          "",
          s"${if (ExternalPlugins.getSeichi915EconomyAPI
                    .hasMoneyAmount(player, productData.getPrice)
                    .getOrElse(false)) s"${ChatColor.GREEN}クリックで購入"
          else s"${ChatColor.RED}十分な金額を所持していません。"}",
          ""
        )
        if (itemMeta.hasLore)
          itemMeta.setLore(itemMeta.getLore.asScala.appendedAll(newLore).asJava)
        else
          itemMeta.setLore(newLore.asJava)
        itemStack.setItemMeta(itemMeta)
        itemStack.setClickAction { player =>
          if (productData.hasEnoughStock && ExternalPlugins.getSeichi915EconomyAPI
                .hasMoneyAmount(player, productData.getPrice)
                .getOrElse(false)) {
            player.getInventory.addItem(productData.getItem)
            ExternalPlugins.getSeichi915EconomyAPI
              .deductMoneyAmount(player, productData.getPrice)
            productData.setStock(
              productData.getStock - productData.getItem.getAmount)
            StoreMenu(shopData).open(player)
          }
        }
        inventory.setItem(productData.getIndex, itemStack)
      }
    val ownerInfoIconSetTask = IO {
      val owner = Bukkit.getOfflinePlayer(shopData.getOwner)
      val ownerInfoIcon = ItemStackBuilder(Material.PLAYER_HEAD)
        .setDisplayName(s"${ChatColor.AQUA}オーナー: ${if (owner.getName.nonNull)
          s"${ChatColor.WHITE}${owner.getName}"
        else s"${ChatColor.RED}${owner.getUniqueId.toString}"}")
        .setSkullOwner(owner)
        .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
        .build
      inventory.setItem(36, ownerInfoIcon)
    }
    val contextShift = IO.contextShift(ExecutionContext.global)
    IO.shift(contextShift)
      .flatMap(_ => ownerInfoIconSetTask)
      .unsafeRunAsyncAndForget()
    player.openInventory(inventory)
  }
}
