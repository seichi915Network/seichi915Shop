package net.seichi915.seichi915shop.listener

import net.seichi915.seichi915shop.Seichi915Shop
import net.seichi915.seichi915shop.inventory.Seichi915ShopStockInventoryHolder
import net.seichi915.seichi915shop.util.Implicits._
import net.seichi915.seichi915shop.util.Util
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.{EventHandler, Listener}
import org.bukkit.persistence.PersistentDataType

import java.util.UUID

class InventoryClickListener extends Listener {
  @EventHandler
  def onInventoryClick(event: InventoryClickEvent): Unit =
    if (event.getInventory.isSeichi915ShopInventory)
      if (!event.getInventory.isSeichi915ShopStockInventory) {
        if (event.getCurrentItem.isNull) return
        if (!event.getWhoClicked.isInstanceOf[Player]) return
        event.setCancelled(true)
        if (event.getCurrentItem.getItemMeta.isNull) return
        if (event.getCurrentItem.getItemMeta.getPersistentDataContainer.isNull)
          return
        if (!event.getCurrentItem.getItemMeta.getPersistentDataContainer.has(
              new NamespacedKey(Seichi915Shop.instance, "click_action"),
              PersistentDataType.STRING)) return
        val uuid = UUID.fromString(
          event.getCurrentItem.getItemMeta.getPersistentDataContainer.get(
            new NamespacedKey(Seichi915Shop.instance, "click_action"),
            PersistentDataType.STRING))
        val clickAction = Seichi915Shop.clickActionMap(uuid)
        clickAction.onClick(event.getWhoClicked.asInstanceOf[Player])
      } else {
        if (event.getCurrentItem.isNull) return
        if (!event.getWhoClicked.isInstanceOf[Player]) return
        val currentItem = event.getCurrentItem.clone()
        val productData = event.getInventory.getHolder
          .asInstanceOf[Seichi915ShopStockInventoryHolder]
          .getProductData
        val productItem = productData.getItem.clone()
        currentItem.setAmount(1)
        productItem.setAmount(1)
        if (!Util.encodeItem(currentItem).equals(Util.encodeItem(productItem)))
          event.setCancelled(true)
      }
}
