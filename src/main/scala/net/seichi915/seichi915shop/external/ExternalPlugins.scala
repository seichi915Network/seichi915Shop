package net.seichi915.seichi915shop.external

import com.sk89q.worldguard.WorldGuard
import com.sk89q.worldguard.bukkit.WorldGuardPlugin
import me.ryanhamshire.GriefPrevention.GriefPrevention
import net.seichi915.seichi915economy.Seichi915Economy
import net.seichi915.seichi915economy.api.Seichi915EconomyAPI

object ExternalPlugins {
  def getSeichi915EconomyAPI: Seichi915EconomyAPI = Seichi915Economy.getAPI

  def getWorldGuard: Option[WorldGuard] =
    if (Seichi915Economy.instance.getServer.getPluginManager.getPlugins
          .map(_.getDescription.getName)
          .contains("WorldGuard"))
      Some(WorldGuard.getInstance())
    else None

  def getWorldGuardPlugin: Option[WorldGuardPlugin] =
    if (Seichi915Economy.instance.getServer.getPluginManager.getPlugins
          .map(_.getDescription.getName)
          .contains("WorldGuard"))
      Some(WorldGuardPlugin.inst())
    else None

  def getGriefPrevention: Option[GriefPrevention] =
    if (Seichi915Economy.instance.getServer.getPluginManager.getPlugins
          .map(_.getDescription.getName)
          .contains("GriefPrevention"))
      Some(GriefPrevention.instance)
    else None
}
