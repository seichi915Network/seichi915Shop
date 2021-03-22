package net.seichi915.seichi915shop.inventory

import net.seichi915.seichi915shop.data.product.ProductData
import org.bukkit.inventory.{Inventory, InventoryHolder}

object Seichi915ShopStockInventoryHolder {
  val seichi915ShopStockInventoryHolder
    : ProductData => Seichi915ShopStockInventoryHolder =
    productData =>
      new Seichi915ShopStockInventoryHolder {
        override def getInventory: Inventory = null

        override def getProductData: ProductData = productData
    }
}

trait Seichi915ShopStockInventoryHolder
    extends InventoryHolder
    with Seichi915ShopInventoryHolder {
  def getProductData: ProductData
}
