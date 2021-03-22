package net.seichi915.seichi915shop.menu

import net.seichi915.seichi915shop.Seichi915Shop
import net.seichi915.seichi915shop.builder.ItemStackBuilder
import net.seichi915.seichi915shop.data.product.ProductData
import net.seichi915.seichi915shop.data.shop.ShopData
import net.seichi915.seichi915shop.inventory.Seichi915ShopInventoryHolder
import net.seichi915.seichi915shop.meta.menu.Menu
import net.seichi915.seichi915shop.util.Implicits._
import org.bukkit.{Bukkit, ChatColor, Material}
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag

case class ProductDeletionConfirmMenu(shopData: ShopData,
                                      productData: ProductData)
    extends Menu {
  override def open(player: Player): Unit = {
    val inventory = Bukkit.createInventory(
      Seichi915ShopInventoryHolder.seichi915ShopInventoryHolder,
      27,
      "商品を削除しますか?")
    val no = ItemStackBuilder(Material.RED_WOOL)
      .setDisplayName(s"${ChatColor.AQUA}いいえ")
      .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
      .build
    no.setClickAction(ProductEditMenu(shopData, productData).open)
    inventory.setItem(12, no)
    val yes = ItemStackBuilder(Material.BLUE_WOOL)
      .setDisplayName(s"${ChatColor.RED}はい")
      .addLore(s"${ChatColor.WHITE}在庫は${ChatColor.RED}削除${ChatColor.WHITE}され、",
               s"${ChatColor.RED}復元することはできません。")
      .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
      .build
    yes.setClickAction { player =>
      Seichi915Shop.productDataToDeleteList =
        Seichi915Shop.productDataToDeleteList.appended(productData)
      Seichi915Shop.productDataList =
        Seichi915Shop.productDataList.filter(_.getUUID != productData.getUUID)
      ShopEditMenu(shopData).open(player)
    }
    inventory.setItem(14, yes)
    player.openInventory(inventory)
  }
}
