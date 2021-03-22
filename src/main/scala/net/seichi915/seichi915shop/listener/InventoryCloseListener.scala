package net.seichi915.seichi915shop.listener

import net.seichi915.seichi915shop.inventory.Seichi915ShopStockInventoryHolder
import net.seichi915.seichi915shop.util.Implicits._
import net.seichi915.seichi915shop.util.Util
import org.bukkit.Material
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.{EventHandler, Listener}

class InventoryCloseListener extends Listener {
  @EventHandler
  def onInventoryClose(event: InventoryCloseEvent): Unit =
    if (event.getInventory.isSeichi915ShopStockInventory) {
      val productData = event.getInventory.getHolder
        .asInstanceOf[Seichi915ShopStockInventoryHolder]
        .getProductData
      val productItem = productData.getItem.clone()
      productItem.setAmount(1)
      var stock = 0
      event.getInventory.getStorageContents.foreach { item =>
        if (item.nonNull && item.getType != Material.AIR) {
          val itemStack = item.clone()
          itemStack.setAmount(1)
          if (Util.encodeItem(productItem).equals(Util.encodeItem(itemStack)))
            stock += item.getAmount
          else
            event.getPlayer.getWorld.dropItem(event.getPlayer.getLocation, item)
        }
      }
      productData.setStock(stock)
    }
}
