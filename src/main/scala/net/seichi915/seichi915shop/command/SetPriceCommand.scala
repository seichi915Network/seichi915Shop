package net.seichi915.seichi915shop.command

import net.seichi915.seichi915shop.Seichi915Shop
import net.seichi915.seichi915shop.util.Implicits._
import org.bukkit.command.{Command, CommandExecutor, CommandSender, TabExecutor}
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil

import java.util
import java.util.Collections
import scala.jdk.CollectionConverters._

class SetPriceCommand extends CommandExecutor with TabExecutor {
  override def onCommand(sender: CommandSender,
                         command: Command,
                         label: String,
                         args: Array[String]): Boolean = {
    if (!sender.isInstanceOf[Player]) {
      sender.sendMessage("このコマンドはプレイヤーのみが実行できます。".toErrorMessage)
      return true
    }
    if (args.length != 3) {
      sender.sendMessage("コマンドの使用法が間違っています。".toErrorMessage)
      return true
    }
    val player = sender.asInstanceOf[Player]
    if (!Seichi915Shop.shopDataList
          .filterNot(_.getOwner != player.getUniqueId)
          .map(_.getName)
          .toSet
          .contains(args(0))) {
      sender.sendMessage(s"ショップ ${args(0)} は見つかりませんでした。".toErrorMessage)
      return true
    }
    val shop = Seichi915Shop.shopDataList
      .filterNot(_.getOwner != player.getUniqueId)
      .filterNot(_.getName != args(0))
      .head
    val index = args(1).toIntOption match {
      case Some(int) => int
      case None =>
        sender.sendMessage("インデックスは0以上35以下で指定してください。".toErrorMessage)
        return true
    }
    if (index < 0 || index > 35) {
      sender.sendMessage("インデックスは0以上35以下で指定してください。".toErrorMessage)
      return true
    }
    val price = args(2).toDoubleOption match {
      case Some(double) => double
      case None =>
        sender.sendMessage("値段は数字で指定してください。".toErrorMessage)
        return true
    }
    if (price < 0.0) {
      sender.sendMessage("値段は0.0以上で指定してください。".toErrorMessage)
      return true
    }
    val productData =
      Seichi915Shop.productDataList.filterNot(_.getShop != shop.getUUID)
    if (!productData.map(_.getIndex).contains(index)) {
      sender.sendMessage("そのインデックスに商品データは存在しません。".toErrorMessage)
      return true
    }
    val product = productData
      .map(data => data.getIndex -> data)
      .toMap
      .apply(index)
    product.setPrice(price)
    sender.sendMessage("価格を変更しました。".toSuccessMessage)
    true
  }

  override def onTabComplete(sender: CommandSender,
                             command: Command,
                             alias: String,
                             args: Array[String]): util.List[String] = {
    if (!sender.isInstanceOf[Player]) return Collections.emptyList()
    val player = sender.asInstanceOf[Player]
    val completions = new util.ArrayList[String]()
    args.length match {
      case 1 =>
        StringUtil.copyPartialMatches(
          args(0),
          Seichi915Shop.shopDataList
            .filterNot(_.getOwner != player.getUniqueId)
            .map(_.getName)
            .asJava,
          completions)
      case 2 =>
        StringUtil.copyPartialMatches(
          args(1),
          (0 until 36).map(_.toString).asJava,
          completions
        )
      case 3 => return Collections.emptyList()
      case _ => return Collections.emptyList()
    }
    Collections.sort(completions)
    completions
  }
}
