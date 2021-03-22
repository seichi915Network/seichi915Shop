package net.seichi915.seichi915shop.data.product

import net.seichi915.seichi915shop.database.Database
import org.bukkit.inventory.ItemStack

import java.util.UUID

case class ProductData(item: ItemStack,
                       var price: Double,
                       var index: Int,
                       var stock: Int,
                       uuid: UUID,
                       shop: UUID) {
  def getItem: ItemStack = item

  def getPrice: Double = price

  def setPrice(price: Double): Unit = this.price = price

  def getIndex: Int = index

  def setIndex(index: Int): Unit = this.index = index

  def getStock: Int = stock

  def setStock(stock: Int): Unit = this.stock = stock

  def getUUID: UUID = uuid

  def getShop: UUID = shop

  def hasEnoughStock: Boolean = getStock - getItem.getAmount >= 0

  def save(): Unit = Database.saveProductData(this)
}
