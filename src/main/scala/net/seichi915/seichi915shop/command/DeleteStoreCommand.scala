package net.seichi915.seichi915shop.command

import net.seichi915.seichi915shop.Seichi915Shop
import net.seichi915.seichi915shop.util.Implicits._
import org.bukkit.command.{Command, CommandExecutor, CommandSender, TabExecutor}
import org.bukkit.entity.Player

import java.util
import java.util.Collections

class DeleteStoreCommand extends CommandExecutor with TabExecutor {
  override def onCommand(sender: CommandSender,
                         command: Command,
                         label: String,
                         args: Array[String]): Boolean = {
    if (!sender.isInstanceOf[Player]) {
      sender.sendMessage("このコマンドはプレイヤーのみが実行できます。".toErrorMessage)
      return true
    }
    if (args.nonEmpty) {
      sender.sendMessage("コマンドの使用法が間違っています。".toErrorMessage)
      return true
    }
    val player = sender.asInstanceOf[Player]
    sender.sendMessage("削除したい店舗を右クリックしてください。".toNormalMessage)
    if (!Seichi915Shop.storeDeletionList.contains(player))
      Seichi915Shop.storeDeletionList =
        Seichi915Shop.storeDeletionList.appended(player)
    if (Seichi915Shop.storeCreationMap.contains(player)) {
      Seichi915Shop.storeCreationMap.remove(player)
      sender.sendMessage("ショップの作成がキャンセルされました。".toWarningMessage)
    }
    true
  }

  override def onTabComplete(sender: CommandSender,
                             command: Command,
                             alias: String,
                             args: Array[String]): util.List[String] =
    Collections.emptyList()
}
