package net.seichi915.seichi915shop.command

import net.seichi915.seichi915shop.Seichi915Shop
import net.seichi915.seichi915shop.util.Implicits._
import org.bukkit.command.{
  Command,
  CommandExecutor,
  CommandSender,
  TabCompleter
}
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil

import java.util
import java.util.Collections
import scala.jdk.CollectionConverters._

class DeleteShopCommand extends CommandExecutor with TabCompleter {
  override def onCommand(sender: CommandSender,
                         command: Command,
                         label: String,
                         args: Array[String]): Boolean = {
    if (!sender.isInstanceOf[Player]) {
      sender.sendMessage("このコマンドはプレイヤーのみが実行できます。".toErrorMessage)
      return true
    }
    if (args.length != 1) {
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
    if (!Seichi915Shop.shopDeletionConfirmationMap.contains((player, shop))) {
      sender.sendMessage(s"ショップ ${shop.getName} を削除しますか?".toWarningMessage)
      sender.sendMessage(
        s"ショップ内のアイテムの取り出しが完了している場合は、30秒以内に /$label ${args.mkString(" ")} と入力してください。".toWarningMessage)
      Seichi915Shop.shopDeletionConfirmationMap += (player, shop) -> 30
    } else {
      Seichi915Shop.shopDataToDeleteList =
        Seichi915Shop.shopDataToDeleteList.appended(shop)
      Seichi915Shop.shopDataList =
        Seichi915Shop.shopDataList.filter(_.getUUID != shop.getUUID)
      Seichi915Shop.productDataToDeleteList =
        Seichi915Shop.productDataToDeleteList.appendedAll(
          Seichi915Shop.productDataList.filterNot(_.getShop != shop.getUUID))
      Seichi915Shop.productDataList =
        Seichi915Shop.productDataList.filter(_.getShop != shop.getUUID)
      sender.sendMessage("ショップの削除が完了しました。".toSuccessMessage)
    }
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
      case _ => return Collections.emptyList()
    }
    Collections.sort(completions)
    completions
  }
}
