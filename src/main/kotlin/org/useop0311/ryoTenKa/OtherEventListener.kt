package org.useop0311.ryoTenKa

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.useop0311.ryoTenKa.RyoTenKa.Companion.PVPTime

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

            if (x >= -250 && x <= 250 && z >= -250 && z <= 250) {
                // 4. 보호 구역 안이라면 공격을 취소!
                event.isCancelled = true

                // 5. 공격한 플레이어에게 알림 메시지 보내기
                damager.sendMessage(
                    Component.text("지금은 스폰 근처에서 PVP가 불가능합니다.", NamedTextColor.GOLD)
                )
            }
        }
    }
}