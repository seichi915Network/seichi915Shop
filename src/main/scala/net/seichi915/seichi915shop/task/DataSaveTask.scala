package net.seichi915.seichi915shop.task

import cats.effect.IO
import net.seichi915.seichi915shop.Seichi915Shop
import net.seichi915.seichi915shop.database.Database
import org.bukkit.scheduler.BukkitRunnable

import scala.concurrent.ExecutionContext

class DataSaveTask extends BukkitRunnable {
  override def run(): Unit = {
    val task = IO {
      Seichi915Shop.shopDataList.foreach { shopData =>
        try shopData.save()
        catch {
          case e: Exception =>
            e.printStackTrace()
            Seichi915Shop.instance.getLogger.warning(
              s"${shopData.getOwner} さんのショップ(${shopData.getName})の保存に失敗しました。")
        }
      }
      Seichi915Shop.shopDataToDeleteList.foreach { shopData =>
        try Database.deleteShopData(shopData)
        catch {
          case e: Exception =>
            e.printStackTrace()
            Seichi915Shop.instance.getLogger.warning(
              s"${shopData.getOwner} さんのショップ(${shopData.getName})の削除に失敗しました。")
        } finally Seichi915Shop.shopDataToDeleteList =
          Seichi915Shop.shopDataToDeleteList.filter(
            _.getUUID != shopData.getUUID)
      }
      Seichi915Shop.productDataList.foreach { productData =>
        try productData.save()
        catch {
          case e: Exception =>
            e.printStackTrace()
            Seichi915Shop.instance.getLogger
              .warning(s"商品(${productData.getUUID})の保存に失敗しました。")
        }
      }
      Seichi915Shop.productDataToDeleteList.foreach { productData =>
        try Database.deleteProductData(productData)
        catch {
          case e: Exception =>
            e.printStackTrace()
            Seichi915Shop.instance.getLogger
              .warning(s"商品(${productData.getUUID})の削除に失敗しました。")
        } finally Seichi915Shop.productDataToDeleteList =
          Seichi915Shop.productDataToDeleteList.filter(
            _.getUUID != productData.getUUID)
      }
      Seichi915Shop.storeDataList.foreach { storeData =>
        try storeData.save()
        catch {
          case e: Exception =>
            e.printStackTrace()
            Seichi915Shop.instance.getLogger
              .warning(s"店舗(${storeData.getUUID})の保存に失敗しました。")
        }
      }
      Seichi915Shop.storeDataToDeleteList.foreach { storeData =>
        try Database.deleteStoreData(storeData)
        catch {
          case e: Exception =>
            e.printStackTrace()
            Seichi915Shop.instance.getLogger
              .warning(s"店舗(${storeData.getUUID})の削除に失敗しました。")
        } finally Seichi915Shop.storeDataToDeleteList =
          Seichi915Shop.storeDataToDeleteList.filter(
            _.getUUID != storeData.getUUID)
      }
    }
    val contextShift = IO.contextShift(ExecutionContext.global)
    IO.shift(contextShift).flatMap(_ => task).unsafeRunAsyncAndForget()
  }
}
