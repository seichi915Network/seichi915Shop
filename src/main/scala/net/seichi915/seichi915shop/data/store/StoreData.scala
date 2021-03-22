package net.seichi915.seichi915shop.data.store

import net.seichi915.seichi915shop.database.Database
import org.bukkit.Location

import java.util.UUID

case class StoreData(location: Location, uuid: UUID, shop: UUID) {
  def getLocation: Location = location

  def getUUID: UUID = uuid

  def getShop: UUID = shop

  def save(): Unit = Database.saveStoreData(this)
}
