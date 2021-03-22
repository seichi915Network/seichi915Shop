package net.seichi915.seichi915shop.menu

import net.seichi915.seichi915shop.Seichi915Shop
import net.seichi915.seichi915shop.builder.ItemStackBuilder
import net.seichi915.seichi915shop.data.shop.ShopData
import net.seichi915.seichi915shop.inventory.Seichi915ShopInventoryHolder
import net.seichi915.seichi915shop.meta.menu.Menu
import net.seichi915.seichi915shop.util.Implicits._
import org.bukkit.{Bukkit, ChatColor, Material}
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag

import scala.jdk.CollectionConverters._

case class ShopEditMenu(shopData: ShopData) extends Menu {
  override def open(player: Player): Unit = {
    val inventory = Bukkit.createInventory(
      Seichi915ShopInventoryHolder.seichi915ShopInventoryHolder,
      45,
      ChatColor.translateAlternateColorCodes(
        '&',
        s"${shopData.getDisplayName}${ChatColor.RESET} - 編集")
    )
    val productData =
      Seichi915Shop.productDataList.filterNot(_.getShop != shopData.getUUID)
    productData.foreach { data =>
      val itemStack = data.getItem.clone()
      val itemMeta = itemStack.getItemMeta
      val newLore = List(
        "",
        s"${ChatColor.GREEN}価格: ${ChatColor.WHITE}${data.getPrice}円",
        s"${if (data.hasEnoughStock) s"${ChatColor.GREEN}在庫あり"
        else s"${ChatColor.RED}在庫なし"}",
        "",
        s"${ChatColor.WHITE}クリックで編集",
        ""
      )
      if (itemMeta.hasLore)
        itemMeta.setLore(itemMeta.getLore.asScala.appendedAll(newLore).asJava)
      else
        itemMeta.setLore(newLore.asJava)
      itemStack.setItemMeta(itemMeta)
      itemStack.setClickAction(ProductEditMenu(shopData, data).open)
      inventory.setItem(data.getIndex, itemStack)
    }
    (0 until 36).foreach { index =>
      if (inventory
            .getItem(index)
            .isNull || inventory.getItem(index).getType == Material.AIR) {
        val addProductButton = ItemStackBuilder(Material.STONE)
          .setDisplayName(s"${ChatColor.AQUA}クリックで商品を追加")
          .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
          .build
        addProductButton.setClickAction { player =>
          player.sendMessage(
            s"商品にしたいアイテムを手に持ち、 /setproduct ${shopData.getName} $index <値段> と入力してください。".toNormalMessage)
          player.closeInventory()
        }
        inventory.setItem(index, addProductButton)
      }
    }
    val closeButton = ItemStackBuilder(Material.BARRIER)
      .setDisplayName(s"${ChatColor.RED}閉じる")
      .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
      .build
    closeButton.setClickAction(_.closeInventory())
    inventory.setItem(44, closeButton)
    player.openInventory(inventory)
  }
}
