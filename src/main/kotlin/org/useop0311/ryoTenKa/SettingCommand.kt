package org.useop0311.ryoTenKa

import com.mojang.brigadier.arguments.ArgumentType
import io.papermc.paper.command.brigadier.argument.ArgumentTypes.player
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import org.useop0311.ryoTenKa.RyoTenKa.Companion.bigTeam
import org.useop0311.ryoTenKa.RyoTenKa.Companion.bigTeamOccur
import org.useop0311.ryoTenKa.RyoTenKa.Companion.unusedTeamColor
import org.useop0311.ryoTenKa.RyoTenKa.Companion.deathCounter

class SettingCommand : CommandExecutor {

    override fun onCommand(sender: CommandSender, cmd: Command, label: String, args: Array<out String>): Boolean {

        val server = sender.server
        val scoreboard = server.scoreboardManager.mainScoreboard

        if(cmd.name.equals("start_game")) {
            if(sender.isOp){
                server.broadcast(
                    Component.text("게임 시작 과정을 시작합니다.")
                )

                // 침대 조합법 제거
                val iterator = server.recipeIterator()
                while (iterator.hasNext()) {
                    val recipe = iterator.next()

                    if (isBed(recipe?.result?.type!!)) {
                        iterator.remove()
//                        logger.info("침대 조합법을 제거했습니다: ${recipe.result.type.name}")
                    }
                }

                // 팀 구성
                for (player in server.onlinePlayers){
                    val team = scoreboard.registerNewTeam(player.name)

                    val random = (Math.random()* unusedTeamColor.count()).toInt()
                    team.color(unusedTeamColor[random])
                    team.addEntry(player.name)

                    unusedTeamColor.removeAt(random)
                    val color = NamedTextColor.NAMES.value(team.color.name.lowercase())

                    player.server.broadcast(
                    Component.text(player.name, NamedTextColor.AQUA)
                        .append(Component.text("님은 지금부터 ", NamedTextColor.GRAY))
                        .append(Component.text(team.name.toString(), color))
                        .append(Component.text("팀입니다!", NamedTextColor.GRAY))
                    )
                }

                server.broadcast(
                    Component.text("게임 시작 과정이 완료되었습니다!")
                )
                server.broadcast(
                    Component.text("랜덤 스폰 카운트다운을 시작합니다!")
                )

                val startCountdown = StartCountdownTask(RyoTenKa.instance, player())
                startCountdown.runTaskTimer(RyoTenKa.instance, 20L, 20L)

                return true;
            } else {
                sender.sendMessage("스타트 명렁어는 관리자만 사용할 수 있습니다.")
                return false;
            }
        }

        if (cmd.name.equals("init_game")) {
            if (sender.isOp) {
                server.broadcast(
                    Component.text("게임 설정 초기화 과정을 시작합니다.")
                )

                for (team in server.scoreboardManager.mainScoreboard.teams) {
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

                bigTeam = null
                bigTeamOccur = false

                server.broadcast(
                    Component.text("게임 설정 초기화 과정이 완료되었습니다!")
                )
                return true;
            } else {
                sender.sendMessage("설정 초기화 명렁어는 관리자만 사용할 수 있습니다.")
                return false;
            }
        }

        if (cmd.name.equals("add_new_team")) {
            if (sender.isOp) {
                if (args.count() > 0){
                    val player = server.getPlayer(args[0])
                    if (player != null) {
                        val scoreboard = server.scoreboardManager.mainScoreboard

                        val team = scoreboard.registerNewTeam(player.name)

                        val random = (Math.random()*unusedTeamColor.count()).toInt()
                        val color = unusedTeamColor[random]

                        team.color(color)
                        team.addEntry(player.name)

                        player.server.broadcast(
                            Component.text(player.name, NamedTextColor.AQUA)
                                .append(Component.text("님은 지금부터 ", NamedTextColor.GRAY))
                                .append(Component.text(team.name, color))
                                .append(Component.text("팀입니다!", NamedTextColor.GRAY))
                        )
                    } else {
                        sender.sendMessage("플레이어 이름을 다시 한번 확인해주세요.")
                    }
                } else {
                    sender.sendMessage("플레이어 이름도 같이 입력해주세요.")
                }
            } else {
                sender.sendMessage("팀 추가 명령어는 관리자만 사용할 수 있습니다.")
            }
        }
        return false
    }

    fun isBed(block : Material) : Boolean {
        if (block == Material.BLUE_BED
            || block == Material.RED_BED
            || block == Material.GRAY_BED
            || block == Material.BLACK_BED
            || block == Material.BROWN_BED
            || block == Material.CYAN_BED
            || block == Material.GREEN_BED
            || block == Material.LIGHT_BLUE_BED
            || block == Material.LIGHT_GRAY_BED
            || block == Material.LIME_BED
            || block == Material.MAGENTA_BED
            || block == Material.ORANGE_BED
            || block == Material.PURPLE_BED
            || block == Material.PINK_BED
            || block == Material.YELLOW_BED
            || block == Material.WHITE_BED ) return true
        else return false
    }
}

class StartCountdownTask(private val plugin: JavaPlugin, player: ArgumentType<PlayerSelectorArgumentResolver>) : BukkitRunnable() {

    private var timeLeft = 10

    override fun run() {
        if (timeLeft > 0) {
            plugin.server.broadcast(
                Component.text(timeLeft.toString(), NamedTextColor.YELLOW)
                    .append(Component.text("초 후에 시작합니다", NamedTextColor.WHITE))
            )
            timeLeft--
        } else {
            plugin.server.broadcast(
                Component.text("게임 시작!", NamedTextColor.RED)
                    .decorate(net.kyori.adventure.text.format.TextDecoration.BOLD)
            )

            for (player in plugin.server.onlinePlayers) {
                deathCounter.putIfAbsent(player.uniqueId, -1)
                deathCounter[player.uniqueId] = -1

                player.health = 0.0
            }
            
            this.cancel()
            return
        }
    }
}
