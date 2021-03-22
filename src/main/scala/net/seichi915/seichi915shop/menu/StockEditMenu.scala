package net.seichi915.seichi915shop.menu

import net.seichi915.seichi915shop.data.product.ProductData
import net.seichi915.seichi915shop.inventory.Seichi915ShopStockInventoryHolder
import net.seichi915.seichi915shop.meta.menu.Menu
import org.bukkit.Bukkit
import org.bukkit.entity.Player

case class StockEditMenu(productData: ProductData) extends Menu {
  override def open(player: Player): Unit = {
    val inventory = Bukkit.createInventory(
      Seichi915ShopStockInventoryHolder.seichi915ShopStockInventoryHolder(
        productData),
      54,
      "在庫設定")
    var stock = productData.getStock
    while (stock >= 64) {
      val itemStack = productData.getItem.clone()
      itemStack.setAmount(64)
      inventory.addItem(itemStack)
      stock -= 64
    }
    val itemStack = productData.getItem.clone()
    itemStack.setAmount(stock)
    inventory.addItem(itemStack)
    player.openInventory(inventory)
  }
}
