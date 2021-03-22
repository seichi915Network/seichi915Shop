package net.seichi915.seichi915shop.util

import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.ItemStack

import java.nio.charset.StandardCharsets
import javax.xml.bind.DatatypeConverter

object Util {
  def encodeItem(itemStack: ItemStack): String = {
    val config = new YamlConfiguration
    config.set("i", itemStack)
    DatatypeConverter.printBase64Binary(
      config.saveToString.getBytes(StandardCharsets.UTF_8))
  }

  def decodeItem(string: String): ItemStack = {
    val config = new YamlConfiguration
    config.loadFromString(
      new String(DatatypeConverter.parseBase64Binary(string),
                 StandardCharsets.UTF_8))
    config.getItemStack("i", null)
  }
}
