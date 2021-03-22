package net.seichi915.seichi915shop.listener

import net.seichi915.seichi915shop.Seichi915Shop
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.{EventHandler, Listener}

class PlayerQuitListener extends Listener {
  @EventHandler
  def onPlayerQuit(event: PlayerQuitEvent): Unit = {
    if (Seichi915Shop.shopDeletionConfirmationMap.keys
          .map(_._1)
          .toSet
          .contains(event.getPlayer)) {
      val shopData = Seichi915Shop.shopDeletionConfirmationMap.keys.toMap
        .apply(event.getPlayer)
      Seichi915Shop.shopDeletionConfirmationMap.remove(
        (event.getPlayer, shopData))
    }
    if (Seichi915Shop.storeCreationMap.contains(event.getPlayer))
      Seichi915Shop.storeCreationMap.remove(event.getPlayer)
    if (Seichi915Shop.storeDeletionList.contains(event.getPlayer))
      Seichi915Shop.storeDeletionList = Seichi915Shop.storeDeletionList.filter(
        _.getUniqueId != event.getPlayer.getUniqueId)
  }
}
