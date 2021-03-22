package net.seichi915.seichi915shop.task

import net.seichi915.seichi915shop.Seichi915Shop
import net.seichi915.seichi915shop.data.shop.ShopData
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable

class ShopDeletionConfirmCountDownTask extends BukkitRunnable {
  override def run(): Unit =
    Seichi915Shop.shopDeletionConfirmationMap.foreach {
      case ((player: Player, shopData: ShopData), remaining: Int) =>
        if (remaining <= 1)
          Seichi915Shop.shopDeletionConfirmationMap.remove((player, shopData))
        else
          Seichi915Shop.shopDeletionConfirmationMap.update((player, shopData),
                                                           remaining - 1)
    }
}
