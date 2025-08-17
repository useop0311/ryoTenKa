package org.useop0311.ryoTenKa

import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.useop0311.ryoTenKa.RyoTenKa.Companion.deathCounter
import org.useop0311.ryoTenKa.RyoTenKa.Companion.unusedTeamColor

class OtherCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (command.name.equals("output_team_color", ignoreCase = true)) {
            if (sender.isOp) {
                val scoreboard = sender.server.scoreboardManager.mainScoreboard

                unusedTeamColor = mutableListOf<NamedTextColor>()
                unusedTeamColor.add(NamedTextColor.RED)
                unusedTeamColor.add(NamedTextColor.GOLD)
                unusedTeamColor.add(NamedTextColor.YELLOW)
                unusedTeamColor.add(NamedTextColor.GREEN)
                unusedTeamColor.add(NamedTextColor.DARK_GREEN)
                unusedTeamColor.add(NamedTextColor.AQUA)
                unusedTeamColor.add(NamedTextColor.DARK_AQUA)
                unusedTeamColor.add(NamedTextColor.BLUE)
                unusedTeamColor.add(NamedTextColor.DARK_BLUE)
                unusedTeamColor.add(NamedTextColor.LIGHT_PURPLE)
                unusedTeamColor.add(NamedTextColor.DARK_PURPLE)
                unusedTeamColor.add(NamedTextColor.WHITE)
                unusedTeamColor.add(NamedTextColor.GRAY)
                unusedTeamColor.add(NamedTextColor.DARK_GRAY)
                unusedTeamColor.add(NamedTextColor.BLACK)
                unusedTeamColor.add(NamedTextColor.DARK_RED)
                unusedTeamColor.add(NamedTextColor.WHITE)

                for (team in scoreboard.teams) {
                    unusedTeamColor.remove(NamedTextColor.NAMES.value(team.color.name.lowercase()))
                }

                sender.sendMessage("팀 색 정보 추출 및 초기화를 완료하였습니다.")
                return true
            } else {
                sender.sendMessage("이 명령어는 관리자만 사용할 수 있습니다.")
                return true
            }
        }

        if (command.name.equals("set_death_count", ignoreCase = true)) {
            if (sender.isOp) {
                if (args.count() == 2) {
                    val player = sender.server.getPlayer(args[0])
                    if (player == null) {
                        sender.sendMessage("올바로 된 플레이어 닉네임을 입력해주세요.")
                        return true
                    }
                    deathCounter[player.uniqueId] = args[1].toInt()
                    sender.sendMessage(player.name + "의 데스 수를 " + args[1].toInt() + "으로 바꿨습니다.")
                    return true
                } else {
                    sender.sendMessage("올바른 형식을 갖춰주세요.")
                    return true
                }
            } else {
                sender.sendMessage("이 명령어는 관리자만 사용할 수 있습니다.")
                return true
            }
        }

        return false
    }
}