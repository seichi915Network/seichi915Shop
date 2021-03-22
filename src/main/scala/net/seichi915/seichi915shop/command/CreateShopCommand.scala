package net.seichi915.seichi915shop.command

import net.seichi915.seichi915shop.Seichi915Shop
import net.seichi915.seichi915shop.data.shop.ShopData
import net.seichi915.seichi915shop.util.Implicits._
import org.bukkit.command.{
  Command,
  CommandExecutor,
  CommandSender,
  TabCompleter
}
import org.bukkit.entity.Player

import java.util
import java.util.{Collections, UUID}

class CreateShopCommand extends CommandExecutor with TabCompleter {
  override def onCommand(sender: CommandSender,
                         command: Command,
                         label: String,
                         args: Array[String]): Boolean = {
    if (!sender.isInstanceOf[Player]) {
      sender.sendMessage("このコマンドはプレイヤーのみが実行できます。".toErrorMessage)
      return true
    }
    if (!(args.length >= 2)) {
      sender.sendMessage("コマンドの使用法が間違っています。".toErrorMessage)
      return true
    }
    val player = sender.asInstanceOf[Player]
    val name = args(0)
    if (!name.matches("[a-zA-Z0-9-_]{1,15}")) {
      sender.sendMessage(
        "ショップ名は1文字以上15文字以内で指定し、英数字とハイフン、アンダースコアが使用できます。".toErrorMessage)
      return true
    }
    if (Seichi915Shop.shopDataList
          .filterNot(_.getOwner != player.getUniqueId)
          .map(_.getName)
          .contains(name)) {
      sender.sendMessage(s"ショップ $name は既に存在します。".toErrorMessage)
      return true
    }
    val displayName = args.drop(1).mkString(" ")
    val shopData =
      ShopData(name, displayName, UUID.randomUUID(), player.getUniqueId)
    Seichi915Shop.shopDataList = Seichi915Shop.shopDataList.appended(shopData)
    sender.sendMessage("ショップを作成しました。".toSuccessMessage)
    true
  }

  override def onTabComplete(sender: CommandSender,
                             command: Command,
                             alias: String,
                             args: Array[String]): util.List[String] =
    Collections.emptyList()
}
