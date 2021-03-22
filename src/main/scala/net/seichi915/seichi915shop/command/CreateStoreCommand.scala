package net.seichi915.seichi915shop.command

import net.seichi915.seichi915shop.Seichi915Shop
import net.seichi915.seichi915shop.util.Implicits._
import org.bukkit.command.{Command, CommandExecutor, CommandSender, TabExecutor}
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil

import java.util
import java.util.Collections
import scala.jdk.CollectionConverters._

class CreateStoreCommand extends CommandExecutor with TabExecutor {
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
    sender.sendMessage("設定したい看板を右クリックしてください。".toNormalMessage)
    if (!Seichi915Shop.storeCreationMap.contains(player))
      Seichi915Shop.storeCreationMap += player -> shop
    if (Seichi915Shop.storeDeletionList.contains(player)) {
      Seichi915Shop.storeDeletionList = Seichi915Shop.storeDeletionList.filter(
        _.getUniqueId != player.getUniqueId)
      sender.sendMessage("ショップの削除がキャンセルされました。".toWarningMessage)
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
