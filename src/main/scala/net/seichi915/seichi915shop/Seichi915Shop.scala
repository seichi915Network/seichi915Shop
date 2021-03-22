package net.seichi915.seichi915shop

import net.seichi915.seichi915shop.command._
import net.seichi915.seichi915shop.data.product.ProductData
import net.seichi915.seichi915shop.data.shop.ShopData
import net.seichi915.seichi915shop.data.store.StoreData
import net.seichi915.seichi915shop.database.Database
import net.seichi915.seichi915shop.external.ExternalPlugins
import net.seichi915.seichi915shop.listener._
import net.seichi915.seichi915shop.meta.menu.ClickAction
import net.seichi915.seichi915shop.task._
import net.seichi915.seichi915shop.util.Implicits._
import org.bukkit.Bukkit
import org.bukkit.block.Sign
import org.bukkit.command.{CommandExecutor, TabCompleter}
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable

import java.util.UUID
import scala.collection.mutable

object Seichi915Shop {
  var instance: Seichi915Shop = _

  var clickActionMap: mutable.HashMap[UUID, ClickAction] = mutable.HashMap()
  var shopDataList: List[ShopData] = List()
  var shopDataToDeleteList: List[ShopData] = List()
  var productDataList: List[ProductData] = List()
  var productDataToDeleteList: List[ProductData] = List()
  var storeDataList: List[StoreData] = List()
  var storeDataToDeleteList: List[StoreData] = List()
  var shopDeletionConfirmationMap: mutable.Map[(Player, ShopData), Int] =
    mutable.Map()
  var storeCreationMap: mutable.Map[Player, ShopData] = mutable.Map()
  var storeDeletionList: List[Player] = List()
}

class Seichi915Shop extends JavaPlugin {
  Seichi915Shop.instance = this

  override def onEnable(): Unit = {
    if (!Database.saveDefaultDatabase) {
      getLogger.severe("デフォルトのデータベースファイルのコピーに失敗しました。サーバーを停止します。")
      Bukkit.shutdown()
      return
    }
    Seq(
      new BlockBreakListener,
      new InventoryClickListener,
      new InventoryCloseListener,
      new PlayerInteractListener,
      new PlayerQuitListener
    ).foreach(Bukkit.getPluginManager.registerEvents(_, this))
    Map(
      "createshop" -> new CreateShopCommand,
      "createstore" -> new CreateStoreCommand,
      "deleteshop" -> new DeleteShopCommand,
      "deletestore" -> new DeleteStoreCommand,
      "editshop" -> new EditShopCommand,
      "modifyshop" -> new ModifyShopCommand,
      "setprice" -> new SetPriceCommand,
      "setproduct" -> new SetProductCommand
    ).foreach {
      case (commandName: String, commandExecutor: CommandExecutor) =>
        Bukkit.getPluginCommand(commandName).setExecutor(commandExecutor)
        Bukkit
          .getPluginCommand(commandName)
          .setTabCompleter(commandExecutor.asInstanceOf[TabCompleter])
    }
    Map(
      (6000, 6000) -> new DataSaveTask,
      (20, 20) -> new ShopDeletionConfirmCountDownTask
    ).foreach {
      case ((delay: Int, period: Int), bukkitRunnable: BukkitRunnable) =>
        bukkitRunnable.runTaskTimer(this, delay, period)
    }
    ExternalPlugins.getWorldGuard match {
      case Some(_) =>
        getLogger.info("WorldGuardがインストールされています。")
      case None =>
        getLogger.warning("WorldGuardが見つかりませんでした。")
    }
    ExternalPlugins.getGriefPrevention match {
      case Some(_) =>
        getLogger.info("GriefPreventionがインストールされています。")
      case None =>
        getLogger.warning("GriefPreventionが見つかりませんでした。")
    }
    Seichi915Shop.shopDataList =
      Seichi915Shop.shopDataList.appendedAll(Database.getShopData)
    getLogger.info("ショップデータの読み込みが完了しました。")
    Seichi915Shop.productDataList =
      Seichi915Shop.productDataList.appendedAll(Database.getProductData)
    getLogger.info("商品データの読み込みが完了しました。")
    Seichi915Shop.storeDataList =
      Seichi915Shop.storeDataList.appendedAll(Database.getStoreData)
    Seichi915Shop.storeDataList.foreach { storeData =>
      if (storeData.getLocation.getWorld.isNull) {
        Seichi915Shop.storeDataToDeleteList =
          Seichi915Shop.storeDataToDeleteList.appended(storeData)
        Seichi915Shop.storeDataList =
          Seichi915Shop.storeDataList.filter(_.getUUID != storeData.getUUID)
      } else {
        val block =
          storeData.getLocation.getWorld.getBlockAt(storeData.getLocation)
        if (block.isNull || !block.getState.isInstanceOf[Sign]) {
          Seichi915Shop.storeDataToDeleteList =
            Seichi915Shop.storeDataToDeleteList.appended(storeData)
          Seichi915Shop.storeDataList =
            Seichi915Shop.storeDataList.filter(_.getUUID != storeData.getUUID)
        }
      }
    }
    getLogger.info("店舗データの読み込みが完了しました。")

    getLogger.info("seichi915Shopが有効になりました。")
  }

  override def onDisable(): Unit = {
    Seichi915Shop.shopDataList.foreach { shopData =>
      try shopData.save()
      catch {
        case e: Exception =>
          e.printStackTrace()
          getLogger.warning(
            s"${shopData.getOwner} さんのショップ(${shopData.getName})の保存に失敗しました。")
      } finally Seichi915Shop.shopDataList =
        Seichi915Shop.shopDataList.filter(_.getUUID != shopData.getUUID)
    }
    Seichi915Shop.shopDataToDeleteList.foreach { shopData =>
      try Database.deleteShopData(shopData)
      catch {
        case e: Exception =>
          e.printStackTrace()
          getLogger.warning(
            s"${shopData.getOwner} さんのショップ(${shopData.getName})の削除に失敗しました。")
      } finally Seichi915Shop.shopDataToDeleteList =
        Seichi915Shop.shopDataToDeleteList.filter(_.getUUID != shopData.getUUID)
    }
    Seichi915Shop.productDataList.foreach { productData =>
      try productData.save()
      catch {
        case e: Exception =>
          e.printStackTrace()
          getLogger.warning(s"商品(${productData.getUUID})の保存に失敗しました。")
      } finally Seichi915Shop.productDataList =
        Seichi915Shop.productDataList.filter(_.getUUID != productData.getUUID)
    }
    Seichi915Shop.productDataToDeleteList.foreach { productData =>
      try Database.deleteProductData(productData)
      catch {
        case e: Exception =>
          e.printStackTrace()
          getLogger.warning(s"商品(${productData.getUUID})の削除に失敗しました。")
      } finally Seichi915Shop.productDataToDeleteList =
        Seichi915Shop.productDataToDeleteList.filter(
          _.getUUID != productData.getUUID)
    }
    Seichi915Shop.storeDataList.foreach { storeData =>
      try storeData.save()
      catch {
        case e: Exception =>
          e.printStackTrace()
          getLogger.warning(s"店舗(${storeData.getUUID})の保存に失敗しました。")
      } finally Seichi915Shop.storeDataList =
        Seichi915Shop.storeDataList.filter(_.getUUID != storeData.getUUID)
    }
    Seichi915Shop.storeDataToDeleteList.foreach { storeData =>
      try Database.deleteStoreData(storeData)
      catch {
        case e: Exception =>
          e.printStackTrace()
          getLogger.warning(s"店舗(${storeData.getUUID})の削除に失敗しました。")
      } finally Seichi915Shop.storeDataToDeleteList =
        Seichi915Shop.storeDataToDeleteList.filter(
          _.getUUID != storeData.getUUID)
    }

    getLogger.info("seichi915Shopが無効になりました。")
  }
}
