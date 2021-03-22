package net.seichi915.seichi915shop.command

import net.seichi915.seichi915shop.Seichi915Shop
import net.seichi915.seichi915shop.util.Implicits._
import org.bukkit.command.{Command, CommandExecutor, CommandSender, TabExecutor}
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil

import java.util
import java.util.Collections
import scala.jdk.CollectionConverters._

class ModifyShopCommand extends CommandExecutor with TabExecutor {
  override def onCommand(sender: CommandSender,
                         command: Command,
                         label: String,
                         args: Array[String]): Boolean = {
    if (!sender.isInstanceOf[Player]) {
      sender.sendMessage("このコマンドはプレイヤーのみが実行できます。".toErrorMessage)
      return true
    }
    if (!(args.length >= 3)) {
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
    val key = args(1)
    val value = args.drop(2).mkString(" ")
    key match {
      case "displayName" =>
        shop.setDisplayName(value)
        sender.sendMessage(
          s"ショップ ${shop.getName} の表示名を $value に設定しました。".toSuccessMessage)
      case _ =>
        sender.sendMessage(s"$key は不明な設定項目です。".toErrorMessage)
        return true
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
      case 2 =>
        StringUtil.copyPartialMatches(args(1),
                                      List("displayName").asJava,
                                      completions)
      case 3 =>
        args(1) match {
          case "displayName" => return Collections.emptyList()
          case _             => return Collections.emptyList()
        }
      case _ => return Collections.emptyList()
    }
    Collections.sort(completions)
    completions
  }
}
