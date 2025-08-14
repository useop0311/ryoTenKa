package org.useop0311.ryoTenKa

import io.papermc.paper.command.brigadier.argument.ArgumentTypes.player
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import org.useop0311.ryoTenKa.RyoTenKa.Companion.deathCounter
import org.useop0311.ryoTenKa.RyoTenKa.Companion.doklib
import org.useop0311.ryoTenKa.RyoTenKa.Companion.unusedTeamColor
import java.util.TimerTask
import kotlin.random.Random

class SpawnZoneCommand : CommandExecutor {
    override fun onCommand(player: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (player !is Player) return false

        if (command.name.equals("escape", ignoreCase = true)) {
            if ((deathCounter[player.uniqueId] ?: 0) >= 10) {
                val x = player.x
                val z = player.z
                val world = player.world.environment

                if (x >= -260 && x <= 260 && z >= -260 && z <= 260 && world == World.Environment.NORMAL) {
                    var heldItem = player.inventory.itemInMainHand
                    if (heldItem.type == Material.DIAMOND_BLOCK) {
                        if (heldItem.amount == 1) {
                            val replaceItem = ItemStack(Material.AIR)
                            player.inventory.setItemInMainHand(replaceItem)
                        } else {
                            val replaceItem = ItemStack(Material.DIAMOND_BLOCK, heldItem.amount - 1)
                            player.inventory.setItemInMainHand(replaceItem)
                        }

                        deathCounter[player.uniqueId] = deathCounter[player.uniqueId]!! - 5

                        player.sendMessage(
                            Component.text("다이아몬드 블럭", NamedTextColor.AQUA)
                                .append(Component.text(" 1개를 사용하여 사망 횟수를 ", NamedTextColor.WHITE))
                                .append(Component.text("5회 차감", NamedTextColor.LIGHT_PURPLE))
                                .append(Component.text("했습니다!", NamedTextColor.WHITE))
                        )
                        player.sendMessage(
                            Component.text("현재 사망 횟수는 ", NamedTextColor.AQUA)
                                .append(Component.text(deathCounter[player.uniqueId]!!, NamedTextColor.RED))
                                .append(Component.text("회 입니다!", NamedTextColor.WHITE))
                        )

                        return true
                    } else {
                        player.sendMessage(
                            Component.text("다이아몬드 블럭", NamedTextColor.AQUA)
                                .append(Component.text("을 들고 명령어를 실행해주세요.", NamedTextColor.WHITE))
                        )

                        return true
                    }
                } else {
                    player.sendMessage(Component.text("명령어를 사용할 수 있는 위치가 아닙니다.", NamedTextColor.RED))
                    return true
                }
            } else {
                player.sendMessage(Component.text("명령어 사용 대상이 아닙니다.", NamedTextColor.RED))
                return true
            }
        }

        if (command.name.equals("doklib", ignoreCase = true)) {
            val heldItem = player.inventory.itemInMainHand

            val x = player.x
            val z = player.z
            val world = player.world.environment
            if (x >= -10 && x <= 10 && z >= -10 && z <= 10 && world == World.Environment.NORMAL) {
                if (heldItem.type == Material.DIAMOND_BLOCK) {
                    if (heldItem.amount >= 9) {
                        if (heldItem.amount == 9) {
                            val replaceItem = ItemStack(Material.AIR)
                            player.inventory.setItemInMainHand(replaceItem)
                        } else {
                            val replaceItem = ItemStack(Material.DIAMOND_BLOCK, heldItem.amount - 1)
                            player.inventory.setItemInMainHand(replaceItem)
                        }

                        val now = System.currentTimeMillis()
                        doklib.putIfAbsent(player.uniqueId, 3)

                        player.server.broadcast(
                            Component.text(player.name, NamedTextColor.RED)
                                .append(Component.text("가 서버 스폰에서 ", NamedTextColor.WHITE))
                                .append(Component.text("독립을 시도합니다!", NamedTextColor.YELLOW))
                        )

                        val doklibCountdown = DoklibTimerTask(RyoTenKa.instance, player)
                        doklibCountdown.runTaskTimer(RyoTenKa.instance, 0L, 1200L)

                        return true
                    } else {
                        player.sendMessage(
                            Component.text("다이아몬드 블럭", NamedTextColor.AQUA)
                                .append(Component.text("이 부족합니다. 9개 이상을 들고 명령어를 실행해주세요.", NamedTextColor.WHITE))
                        )
                        return true
                    }
                } else {
                    player.sendMessage(
                        Component.text("다이아몬드 블럭", NamedTextColor.AQUA)
                            .append(Component.text("을 들고 명령어를 실행해주세요.", NamedTextColor.WHITE))
                    )
                    return true
                }
            } else {
                player.sendMessage(Component.text("명령어를 사용할 수 있는 위치가 아닙니다.", NamedTextColor.RED))
                return true
            }
        }

        return false
    }
}

class DoklibTimerTask(private val plugin: JavaPlugin, val player : Player) : BukkitRunnable() {
    override fun run() {
        if (doklib.containsKey(player.uniqueId)) {
            if (doklib[player.uniqueId]!! < 1) {
                // 독립 성공
                val scoreboard = player.server.scoreboardManager.mainScoreboard

                val team = scoreboard.registerNewTeam(player.name)

                val random = (Math.random()*unusedTeamColor.count()).toInt()
                val color = unusedTeamColor[random]

                team.color(color)
                team.addEntry(player.name)

                player.server.broadcast(
                    Component.text(player.name, NamedTextColor.AQUA)
                        .append(Component.text("가 독립에 성공했습니다!", NamedTextColor.WHITE))
                )
                player.server.broadcast(
                    Component.text(player.name, NamedTextColor.AQUA)
                        .append(Component.text("님은 지금부터 ", NamedTextColor.GRAY))
                        .append(Component.text(team.name, color))
                        .append(Component.text("팀입니다!", NamedTextColor.GRAY))
                )

                doklib.remove(player.uniqueId)
                this.cancel()

                return
            }

            player.server.broadcast(
                Component.text(player.name, NamedTextColor.YELLOW)
                    .append(Component.text("의 독립까지 남은 시간 : ", NamedTextColor.WHITE))
                    .append(Component.text(doklib[player.uniqueId].toString(), NamedTextColor.RED))
                    .append(Component.text("분", NamedTextColor.WHITE))
            )

            doklib[player.uniqueId] = doklib[player.uniqueId]!! - 1
        } else {
            // player.server.broadcast(Component.text("오류 발생"))
            this.cancel()
            return
        }
    }
}