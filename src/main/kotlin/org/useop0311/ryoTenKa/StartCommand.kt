package org.useop0311.ryoTenKa

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class StartCommand : CommandExecutor {

    override fun onCommand(sender: CommandSender, cmd: Command, label: String, args: Array<out String>): Boolean {

//        val scoreboard = sender.server.scoreboardManager.mainScoreboard

        if(cmd.name.equals("start")) {
            if(sender.isOp){
                return true;
            } else {
                sender.sendMessage("커맨드는 관리자만 사용 가능합니다.")
                return false;
            }
        }

        return false
    }
}