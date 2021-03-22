package net.seichi915.seichi915shop.inventory

import org.bukkit.inventory.{Inventory, InventoryHolder}

object Seichi915ShopInventoryHolder {
  val seichi915ShopInventoryHolder: Seichi915ShopInventoryHolder =
    new Seichi915ShopInventoryHolder {
      override def getInventory: Inventory = null
    }
}

trait Seichi915ShopInventoryHolder extends InventoryHolder
