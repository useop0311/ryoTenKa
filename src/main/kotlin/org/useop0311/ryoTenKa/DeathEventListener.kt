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
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.useop0311.ryoTenKa.RyoTenKa.Companion.bigTeamOccur
import org.useop0311.ryoTenKa.RyoTenKa.Companion.bigTeam
import org.useop0311.ryoTenKa.RyoTenKa.Companion.PVPTime

class DeathEventListener : Listener {

    val deathCounter: HashMap<Player, Int> = HashMap()

    @EventHandler
    fun onDeath(event: PlayerDeathEvent) {
        val scoreboard = event.player.server.scoreboardManager.mainScoreboard

        // 죽은 플레이어
        val victim = event.player
        deathCounter.let {
            if (deathCounter.containsKey(victim)) {
                deathCounter[victim] = deathCounter[victim]!! + 1
            } else {
                deathCounter[victim] = 1
            }
        }

        // 죽인 플레이어 (null일 수 있음!)
        val killer = victim.killer // victim.getKiller() 와 같음

        // killer가 null이 아니고, 플레이어일 때만 실행
        if (killer != null) {
            // it은 여기서 killer 객체를 가리킴
            val victimName = victim.name
            val victimTeam = scoreboard.getEntryTeam(victimName)
            val victimTeamName = victimTeam?.name
            val killerName = killer.name
            val killerTeam = scoreboard.getEntryTeam(killerName)
            val killerTeamName = killerTeam?.name

//            if (victimTeam == killerTeam) return

            // 서버 전체에 메시지 보내기
            victim.server.broadcast(
                Component.text(killerTeamName ?: "알수없음", NamedTextColor.RED)
                    .append(Component.text("팀의 ", NamedTextColor.GRAY))
                    .append(Component.text(killerName, NamedTextColor.RED))
                    .append(Component.text("님이 ", NamedTextColor.GRAY))
                    .append(Component.text(victimTeamName ?: "알수없음", NamedTextColor.AQUA))
                    .append(Component.text("팀의 ", NamedTextColor.GRAY))
                    .append(Component.text(victimName, NamedTextColor.AQUA))
                    .append(Component.text(" 님을 처치했습니다!", NamedTextColor.GRAY))
            )

            //move player's team
            killerTeam?.addEntries(victimName)
            victim.sendMessage(
                Component.text(victimName, NamedTextColor.AQUA)
                    .append(Component.text("님은 지금부터 ", NamedTextColor.GRAY))
                    .append(Component.text(killerTeamName ?: "알수없음", NamedTextColor.RED))
                    .append(Component.text("팀입니다!", NamedTextColor.GRAY))
            )

            event.deathMessage(null)
        }
        return
    }

    @EventHandler
    fun onPlayerRespawn(event: PlayerRespawnEvent) {
        val player = event.player
        val scoreboard = player.server.scoreboardManager.mainScoreboard

        if ((deathCounter[player] ?: 0) > 10) {
            // 디버프 구문 추가해야함
            // 잠정보류
        }
    }
}