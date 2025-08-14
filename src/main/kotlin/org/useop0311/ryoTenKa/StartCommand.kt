package org.useop0311.ryoTenKa

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import org.org.useop0311.ryoTenKa.RyoTenKa.Companion.instance
import org.org.useop0311.ryoTenKa.RyoTenKa.Companion.unusedTeamColor
import org.org.useop0311.ryoTenKa.RyoTenKa.Companion.deathCounter

// TODO : StartCommand -> SettingCommand 이름변경
class StartCommand : CommandExecutor {

    override fun onCommand(sender: CommandSender, cmd: Command, label: String, args: Array<out String>): Boolean {

        val scoreboard = server.scoreboardManager.mainScoreboard
        val server = sender.server

        if(cmd.name.equals("start")) {
            if(sender.isOp){
                server.broadcast(
                    Component.text("게임 시작 과정을 시작합니다.")
                )

                // 침대 조합법 제거
                logger.info("조합법 정리 시작")
                val iterator = server.recipeIterator()
                while (iterator.hasNext()) {
                    val recipe = iterator.next()

                    if (isBed(recipe?.result?.type!!)) {
                        iterator.remove()
                        logger.info("침대 조합법을 제거했습니다: ${recipe.result.type.name}")
                    }
                }
                logger.info("조합법 정리 완료")

                // 팀 구성
                for (player in server.getOnlinePlayer()){
                    val team = scoreboard.registerNewTeam(player.name)

                    val random = (Math.random()* unusedTeamColor.count()).toInt()
                    team.color(unusedTeamColor[random])
                    team.addEntry(player.name)

                    unusedTeamColor.removeAt(random)

                    player.server.broadcast(
                    Component.text(player.name, NamedTextColor.AQUA)
                        .append(Component.text("님은 지금부터 ", NamedTextColor.GRAY))
                        .append(Component.text(team.name, color))
                        .append(Component.text("팀입니다!", NamedTextColor.GRAY))
                    )
                }

                server.broadcast(
                    Component.text("게임 시작 과정이 완료되었습니다!")
                )
                server.broadcast(
                    Component.text("랜덤 스폰 카운트다운을 시작합니다!")
                )

                val startCountdown = StartTimerTask(RyoTenKa.instance, player)
                startCountdown.runTaskTimer(RyoTenKa.instance, 20L, 20L)

                return true;
            } else {
                sender.sendMessage("스타트 명렁어는 관리자만 사용할 수 있습니다.")
                return false;
            }
        }

        if (cmd.name.equals("init_settings")){

        }if(sender.isOp){
                server.broadcast(
                    Component.text("게임 설정 초기화 과정을 시작합니다.")
                )

                logger.info("팀 관련 초기화 시작")
                for (team in server.scoreboardManager.mainScoreboard.teams){
                        team.unregister()
                }

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
                logger.info("팀 관련 초기화 완료")

                server.broadcast(
                    Component.text("게임 설정 초기화 과정이 완료되었습니다!")
                )
                return true;
            } else {
                sender.sendMessage("설정 초기화 명렁어는 관리자만 사용할 수 있습니다.")
                return false;
            }

        return false
    }
}

class StartCountdownTask(private val plugin: JavaPlugin) : BukkitRunnable() {

    private var timeLeft = 10

    override fun run() {
        if (timeLeft > 0) {
            plugin.server.broadcast(
                Component.text(timeLeft.toString(), NamedTextColor.YELLOW)
                    .append(Component.text("후에 시작합니다"), NamedTextColor.WHITE)
            )
            timeLeft--
        } else {
            plugin.server.broadcast(
                Component.text("게임 시작!", NamedTextColor.RED)
                    .decorate(net.kyori.adventure.text.format.TextDecoration.BOLD)
            )

            for (player in plugin.server.getOnlinePlayer()) {
                deathCounter.putIfAbsent(victim.uniqueId, -1)
                deathCounter[victim.uniqueId] = -1

                player.health(0.0)
            }
            
            this.cancel()
            return
        }
    }
}