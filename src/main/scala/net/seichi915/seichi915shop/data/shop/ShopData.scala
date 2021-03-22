package net.seichi915.seichi915shop.data.shop

import net.seichi915.seichi915shop.database.Database

import java.util.UUID

case class ShopData(name: String,
                    var displayName: String,
                    uuid: UUID,
                    owner: UUID) {
  def getName: String = name

  def getDisplayName: String = displayName

  def setDisplayName(displayName: String): Unit = this.displayName = displayName

  def getUUID: UUID = uuid

  def getOwner: UUID = owner

  def save(): Unit = Database.saveShopData(this)
}
