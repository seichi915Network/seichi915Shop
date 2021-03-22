package net.seichi915.seichi915shop.database

import net.seichi915.seichi915shop.Seichi915Shop
import net.seichi915.seichi915shop.data.product.ProductData
import net.seichi915.seichi915shop.data.shop.ShopData
import net.seichi915.seichi915shop.data.store.StoreData
import net.seichi915.seichi915shop.util.Util
import org.bukkit.{Bukkit, Location}
import scalikejdbc._

import java.io.{File, FileOutputStream}
import java.util.UUID

object Database {
  Class.forName("org.sqlite.JDBC")

  private val dbName = Seichi915Shop.instance.getDescription.getName.toLowerCase

  ConnectionPool.add(
    dbName,
    s"jdbc:sqlite:${Seichi915Shop.instance.getDataFolder.getAbsolutePath}/database.db",
    "",
    "")

  GlobalSettings.loggingSQLAndTime = LoggingSQLAndTimeSettings(
    enabled = false
  )

  def saveDefaultDatabase: Boolean =
    try {
      if (!Seichi915Shop.instance.getDataFolder.exists())
        Seichi915Shop.instance.getDataFolder.mkdir()
      val databaseFile =
        new File(Seichi915Shop.instance.getDataFolder, "database.db")
      if (!databaseFile.exists()) {
        val inputStream =
          Seichi915Shop.instance.getResource("database.db")
        val outputStream = new FileOutputStream(databaseFile)
        val bytes = new Array[Byte](1024)
        var read = 0
        while ({
          read = inputStream.read(bytes)
          read
        } != -1) outputStream.write(bytes, 0, read)
        inputStream.close()
        outputStream.close()
      }
      true
    } catch {
      case e: Exception =>
        e.printStackTrace()
        false
    }

  def getShopData: List[ShopData] =
    NamedDB(dbName) localTx { implicit session =>
      sql"SELECT * FROM shopdata"
        .map { resultSet =>
          ShopData(
            resultSet.string("name"),
            resultSet.string("display_name"),
            UUID.fromString(resultSet.string("uuid")),
            UUID.fromString(resultSet.string("owner"))
          )
        }
        .list()
        .apply()
    }

  def getProductData: List[ProductData] =
    NamedDB(dbName) localTx { implicit session =>
      sql"SELECT * FROM productdata"
        .map { resultSet =>
          ProductData(
            Util.decodeItem(resultSet.string("item")),
            resultSet.double("price"),
            resultSet.int("item_index"),
            resultSet.int("stock"),
            UUID.fromString(resultSet.string("uuid")),
            UUID.fromString(resultSet.string("shop_uuid"))
          )
        }
        .list()
        .apply()
    }

  def getStoreData: List[StoreData] =
    NamedDB(dbName) localTx { implicit session =>
      sql"SELECT * FROM storedata"
        .map { resultSet =>
          StoreData(
            new Location(
              Bukkit.getWorld(resultSet.string("world")),
              resultSet.int("x"),
              resultSet.int("y"),
              resultSet.int("z")
            ),
            UUID.fromString(resultSet.string("uuid")),
            UUID.fromString(resultSet.string("shop_uuid"))
          )
        }
        .list()
        .apply()
    }

  def saveShopData(shopData: ShopData): Unit = {
    val isShopDataExist = NamedDB(dbName) localTx { implicit session =>
      sql"SELECT name FROM shopdata WHERE uuid = ${shopData.getUUID}"
        .map(_.string("name"))
        .list()
        .apply()
        .nonEmpty
    }
    if (isShopDataExist)
      NamedDB(dbName) localTx { implicit session =>
        sql"UPDATE shopdata SET display_name = ${shopData.getDisplayName} WHERE uuid = ${shopData.getUUID}"
          .update()
          .apply()
      } else
      NamedDB(dbName) localTx { implicit session =>
        sql"INSERT INTO shopdata (name, display_name, uuid, owner) VALUES (${shopData.getName}, ${shopData.getDisplayName}, ${shopData.getUUID}, ${shopData.getOwner})"
          .update()
          .apply()
      }
  }

  def deleteShopData(shopData: ShopData): Unit = {
    val isShopDataExist = NamedDB(dbName) localTx { implicit session =>
      sql"SELECT name FROM shopdata WHERE uuid = ${shopData.getUUID}"
        .map(_.string("name"))
        .list()
        .apply()
        .nonEmpty
    }
    if (isShopDataExist)
      NamedDB(dbName) localTx { implicit session =>
        sql"DELETE FROM shopdata WHERE uuid = ${shopData.getUUID}"
          .update()
          .apply()
      }
  }

  def saveProductData(productData: ProductData): Unit = {
    val isProductDataExist = NamedDB(dbName) localTx { implicit session =>
      sql"SELECT stock FROM productdata WHERE uuid = ${productData.getUUID}"
        .map(_.int("stock"))
        .list()
        .apply()
        .nonEmpty
    }
    if (isProductDataExist)
      NamedDB(dbName) localTx { implicit session =>
        sql"UPDATE productdata SET price = ${productData.getPrice}, item_index = ${productData.getIndex}, stock = ${productData.getStock} WHERE uuid = ${productData.getUUID}"
          .update()
          .apply()
      } else
      NamedDB(dbName) localTx { implicit session =>
        sql"INSERT INTO productdata (item, price, item_index, stock, uuid, shop_uuid) VALUES (${Util.encodeItem(
          productData.getItem)}, ${productData.getPrice}, ${productData.getIndex}, ${productData.getStock}, ${productData.getUUID}, ${productData.getShop})"
          .update()
          .apply()
      }
  }

  def deleteProductData(productData: ProductData): Unit = {
    val isProductDataExist = NamedDB(dbName) localTx { implicit session =>
      sql"SELECT stock FROM productdata WHERE uuid = ${productData.getUUID}"
        .map(_.int("stock"))
        .list()
        .apply()
        .nonEmpty
    }
    if (isProductDataExist)
      NamedDB(dbName) localTx { implicit session =>
        sql"DELETE FROM productdata WHERE uuid = ${productData.getUUID}"
          .update()
          .apply()
      }
  }

  def saveStoreData(storeData: StoreData): Unit = {
    val isStoreDataExist = NamedDB(dbName) localTx { implicit session =>
      sql"SELECT world FROM storedata WHERE uuid = ${storeData.getUUID}"
        .map(_.string("world"))
        .list()
        .apply()
        .nonEmpty
    }
    if (!isStoreDataExist)
      NamedDB(dbName) localTx { implicit session =>
        sql"INSERT INTO storedata (world, x, y, z, uuid, shop_uuid) VALUES (${storeData.getLocation.getWorld.getName}, ${storeData.getLocation.getBlockX}, ${storeData.getLocation.getBlockY}, ${storeData.getLocation.getBlockZ}, ${storeData.getUUID}, ${storeData.getShop})"
          .update()
          .apply()
      }
  }

  def deleteStoreData(storeData: StoreData): Unit = {
    val isStoreDataExist = NamedDB(dbName) localTx { implicit session =>
      sql"SELECT world FROM storedata WHERE uuid = ${storeData.getUUID}"
        .map(_.string("world"))
        .list()
        .apply()
        .nonEmpty
    }
    if (isStoreDataExist)
      NamedDB(dbName) localTx { implicit session =>
        sql"DELETE FROM storedata WHERE uuid = ${storeData.getUUID}"
          .update()
          .apply()
      }
  }
}
