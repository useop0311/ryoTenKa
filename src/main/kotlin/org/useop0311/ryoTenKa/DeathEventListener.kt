package org.useop0311.ryoTenKa

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.useop0311.ryoTenKa.RyoTenKa.Companion.deathCounter
import org.useop0311.ryoTenKa.RyoTenKa.Companion.doklib
import javax.inject.Named
import javax.naming.Name

class DeathEventListener : Listener {


    @EventHandler
    fun onDeath(event: PlayerDeathEvent) {
        val scoreboard = event.player.server.scoreboardManager.mainScoreboard

        // 죽은 플레이어
        val victim = event.player
        val victimName = victim.name
        deathCounter.putIfAbsent(victim.uniqueId, 1)
        deathCounter[victim.uniqueId] = deathCounter[victim.uniqueId]!! + 1

        // 죽인 플레이어 (null일 수 있음!)
        val killer = victim.killer // victim.getKiller() 와 같음

        // killer가 null이 아니고, 플레이어일 때만 실행
        if (killer != null) {
            // it은 여기서 killer 객체를 가리킴
            val victimTeam = scoreboard.getEntryTeam(victimName)
            val victimTeamName = victimTeam?.name
            lateinit var victimTeamColor: NamedTextColor
            if (victimTeamName != null) {
                victimTeamColor = NamedTextColor.NAMES.value(victimTeam.color.name.lowercase())!!
            } else {
                victimTeamColor = NamedTextColor.AQUA
            }
            val killerName = killer.name
            val killerTeam = scoreboard.getEntryTeam(killerName)
            val killerTeamName = killerTeam?.name
            lateinit var killerTeamColor: NamedTextColor
            if (killerTeamName != null) {
                killerTeamColor = NamedTextColor.NAMES.value(killerTeam.color.name.lowercase())!!
            } else {
                killerTeamColor = NamedTextColor.RED
            }

//            if (victimTeam == killerTeam) return

            // 서버 전체에 메시지 보내기
            victim.server.broadcast(
                Component.text(killerTeamName ?: "알수없음", killerTeamColor)
                    .append(Component.text("팀의 ", NamedTextColor.GRAY))
                    .append(Component.text(killerName, NamedTextColor.RED))
                    .append(Component.text("님이 ", NamedTextColor.GRAY))
                    .append(Component.text(victimTeamName ?: "알수없음", victimTeamColor))
                    .append(Component.text("팀의 ", NamedTextColor.GRAY))
                    .append(Component.text(victimName, NamedTextColor.AQUA))
                    .append(Component.text(" 님을 처치했습니다!", NamedTextColor.GRAY))
            )

            //move player's team
            killerTeam?.addEntries(victimName)
            victim.server.broadcast(
                Component.text(victimName, NamedTextColor.AQUA)
                    .append(Component.text("님은 지금부터 ", NamedTextColor.GRAY))
                    .append(Component.text(killerTeamName ?: "알수없음", killerTeamColor))
                    .append(Component.text("팀입니다!", NamedTextColor.GRAY))
            )

            event.deathMessage(null)
        }

        // 죽은 플레이어가 독립중
        if (doklib[victim.uniqueId] != null) {
            victim.server.broadcast(
                Component.text(victimName, NamedTextColor.AQUA)
                    .append(Component.text("님이 독립에 실패하였습니다!", NamedTextColor.YELLOW))
            )

            doklib.remove(victim.uniqueId)
        }

        return
    }

    @EventHandler
    fun onPlayerRespawn(event: PlayerRespawnEvent) {
        val player = event.player
        val scoreboard = player.server.scoreboardManager.mainScoreboard

        player.sendMessage(
            Component.text("누적 사망 횟수는 ", NamedTextColor.WHITE)
                .append(Component.text(deathCounter[player.uniqueId].toString(), NamedTextColor.RED))
                .append(Component.text("회 입니다.", NamedTextColor.WHITE))
        )

        if ((deathCounter[player.uniqueId] ?: 0) == 10) {
            // 누적 10데스 시 중앙 지역 진입 불가
            // 패널티 해제 방법: 다이아몬드블럭 중앙지역에 기부

            player.sendMessage(Component.text("누적 사망 횟수 10회를 달성하여 스폰 구역 진입이 금지되었습니다.",NamedTextColor.RED))
            player.sendMessage(Component.text("다이아 블럭 1개를 들고 /escape 명령어를 사용하여 사망 횟수를 차감하십시오. ",NamedTextColor.RED))
        }
    }
}