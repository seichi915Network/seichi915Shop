package net.seichi915.seichi915shop.menu

import net.seichi915.seichi915shop.builder.ItemStackBuilder
import net.seichi915.seichi915shop.data.product.ProductData
import net.seichi915.seichi915shop.data.shop.ShopData
import net.seichi915.seichi915shop.inventory.Seichi915ShopInventoryHolder
import net.seichi915.seichi915shop.meta.menu.Menu
import net.seichi915.seichi915shop.util.Implicits._
import org.bukkit.{Bukkit, ChatColor, Material}
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag

case class ProductEditMenu(shopData: ShopData, productData: ProductData)
    extends Menu {
  override def open(player: Player): Unit = {
    val inventory = Bukkit.createInventory(
      Seichi915ShopInventoryHolder.seichi915ShopInventoryHolder,
      27,
      "商品設定")
    val setPriceButton = ItemStackBuilder(Material.EMERALD)
      .setDisplayName(s"${ChatColor.AQUA}価格を変更")
      .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
      .build
    setPriceButton.setClickAction { player =>
      player.sendMessage(
        s"チャット欄に /setprice ${shopData.getName} ${productData.getIndex} <新しい価格> と入力してください。".toNormalMessage)
      player.closeInventory()
    }
    inventory.setItem(10, setPriceButton)
    val editStockButton = ItemStackBuilder(Material.CHEST)
      .setDisplayName(s"${ChatColor.AQUA}在庫設定")
      .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
      .build
    editStockButton.setClickAction(StockEditMenu(productData).open)
    inventory.setItem(13, editStockButton)
    val deleteProductButton = ItemStackBuilder(Material.LAVA_BUCKET)
      .setDisplayName(s"${ChatColor.AQUA}商品を削除")
      .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
      .build
    deleteProductButton.setClickAction(
      ProductDeletionConfirmMenu(shopData, productData).open)
    inventory.setItem(16, deleteProductButton)
    val backButton = ItemStackBuilder(Material.BARRIER)
      .setDisplayName(s"${ChatColor.RED}戻る")
      .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
      .build
    backButton.setClickAction(ShopEditMenu(shopData).open)
    inventory.setItem(26, backButton)
    player.openInventory(inventory)
  }
}
