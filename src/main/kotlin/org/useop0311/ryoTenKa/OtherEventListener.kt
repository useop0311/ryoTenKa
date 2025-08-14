package org.useop0311.ryoTenKa

import io.papermc.paper.command.brigadier.argument.ArgumentTypes.world
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.useop0311.ryoTenKa.RyoTenKa.Companion.PVPTime
import org.useop0311.ryoTenKa.RyoTenKa.Companion.deathCounter
import org.useop0311.ryoTenKa.RyoTenKa.Companion.doklib

class OtherEventListener : Listener {
    @EventHandler
    fun onLeftClick(event: PlayerInteractEvent) {
        if (event.action != Action.RIGHT_CLICK_BLOCK) {
            return
        }

        val clickedBlock = event.clickedBlock ?: return

        if (clickedBlock.type == Material.BLUE_BED
            || clickedBlock.type == Material.RED_BED
            || clickedBlock.type == Material.GRAY_BED
            || clickedBlock.type == Material.BLACK_BED
            || clickedBlock.type == Material.BROWN_BED
            || clickedBlock.type == Material.CYAN_BED
            || clickedBlock.type == Material.GREEN_BED
            || clickedBlock.type == Material.LIGHT_BLUE_BED
            || clickedBlock.type == Material.LIGHT_GRAY_BED
            || clickedBlock.type == Material.LIME_BED
            || clickedBlock.type == Material.MAGENTA_BED
            || clickedBlock.type == Material.ORANGE_BED
            || clickedBlock.type == Material.PURPLE_BED
            || clickedBlock.type == Material.PINK_BED
            || clickedBlock.type == Material.YELLOW_BED
            || clickedBlock.type == Material.WHITE_BED
        ) {
            val player = event.player
            clickedBlock.setType(Material.AIR)
            event.isCancelled = true
            player.playSound(player.location, Sound.BLOCK_WOOD_BREAK, 1.0f, 1.0f)
        }
    }

    @EventHandler
    fun onPlayerAttackInZone(event: EntityDamageByEntityEvent) {
        val damager = event.damager
        val victim = event.entity

        if (damager is Player && victim is Player && PVPTime) {
            val victimLocation = victim.location
            val x = victimLocation.x
            val z = victimLocation.z
            val world = victimLocation.world.environment

            if (x >= -250 && x <= 250 && z >= -250 && z <= 250 && world == World.Environment.NORMAL) {
                event.isCancelled = true

                damager.sendMessage(
                    Component.text("지금은 스폰 근처에서 PVP가 불가능합니다.", NamedTextColor.GOLD)
                )
            }
        }
    }

    @EventHandler
    fun onPlayerMoveToZone(event: PlayerMoveEvent) {
        val player = event.player

        if (deathCounter.contains(player.uniqueId)) {
            if (deathCounter[player.uniqueId]!! >= 10) {
                val x = player.x
                val z = player.z
                val world = player.world.environment

                if (x >= -250 && x <= 250 && z >= -250 && z <= 250 && world == World.Environment.NORMAL) {
                    player.damage(3.0)
                    player.sendMessage(Component.text("당장 나오세요! 죽은 횟수가 10회가 넘어 스폰구역 진입이 불가능합니다.\n/escape 명령어를 이용해서 제한을 해제하세요.", NamedTextColor.RED))
                }
            }
        }

        if (doklib[player.uniqueId] != null) {
            val x = player.x
            val z = player.z
            val world = player.world.environment

            if (x < -100 || x > 100 || z < -100 || z > 100 && world == World.Environment.NORMAL) {
                player.damage(3.0)
                player.sendMessage(Component.text("당장 들어오세요! 독립 과정 진행중에는 밖으로 나갈 수 없습니다.", NamedTextColor.RED))
            }
        }
    }
}