package org.useop0311.ryoTenKa

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scoreboard.Team
import java.io.*
import java.time.LocalTime
import java.util.*


class RyoTenKa : JavaPlugin() {
    companion object {
        var unusedTeamColor = mutableListOf<NamedTextColor>()

        var bigTeamOccur : Boolean = false
        var bigTeam : Team? = null

        var PVPTime : Boolean = false

        val deathCounter: HashMap<UUID, Int> = HashMap()

        var doklib : HashMap<UUID, Long> = HashMap()
        lateinit var instance: RyoTenKa
    }

    private val deathF = File(dataFolder, "/deathData.txt")

    override fun onEnable() {
        // Plugin startup logic
        instance = this

        // dataFile
        makeFile(deathF);
        fileToMap(deathF, deathCounter)

        // Commands
        getCommand("start_game")?.setExecutor(SettingCommand())
        getCommand("init_game")?.setExecutor(SettingCommand())
        getCommand("add_new_team")?.setExecutor(SettingCommand())
        getCommand("escape")?.setExecutor(SpawnZoneCommand())
        getCommand("doklib")?.setExecutor(SpawnZoneCommand())

        // EventHandle
        server.pluginManager.registerEvents(DeathEventListener(), this)
        server.pluginManager.registerEvents(OtherEventListener(), this)

        // Scheduler
        mapToFileSchedule(deathF, deathCounter)
        spawnPVPSchedule()
        bigTeamSchedule()

        logger.info("RYO TENKA 활성화됨")
    }

    override fun onDisable() {
        // Plugin shutdown logic
        mapToFile(deathF, deathCounter)
        logger.info("RYO TENKA 비활성화됨")
    }

    fun spawnPVPSchedule() {
        val delay = 0L
        val period = 1200L // 1분 = 60초 * 20틱 = 1200틱
        server.scheduler.runTaskTimer(this, Runnable {
            val now = LocalTime.now()
            val currentHour = now.hour

            if (currentHour == 20) {
                if (!PVPTime) {
                    PVPTime = true
                    server.broadcast(Component.text("지금부터 1시간동안 스폰구역 500블럭 내에서 PVP가 불가능합니다.", NamedTextColor.AQUA))
                }
            } else {
                if (PVPTime) {
                    PVPTime = false
                    server.broadcast(Component.text("지금부터 다시 스폰구역 500블럭 내에서 PVP가 가능합니다.", NamedTextColor.AQUA))
                }
            }
        }, delay, period)
    }

    fun bigTeamSchedule(){
        val delay = 0L
        val period = 60L

//        logger.info("스케쥴러 접속")

        // find + glowing
        server.scheduler.runTaskTimer(this, Runnable {
//            logger.info("스케쥴러 내")
            if (bigTeamOccur) {
//                logger.info("대세력 확인")
                if (bigTeam!!.entries.count() <= 7){
                    bigTeamOccur = false
                    val color = NamedTextColor.NAMES.value(bigTeam!!.color.name.lowercase())

                    server.broadcast(
                        Component.text("대세력 소멸! 지금부터 ", NamedTextColor.WHITE)
                            .append(Component.text(bigTeam?.name ?: "알수없음", color))
                            .append(Component.text("팀은 대세력 디버프가 해제됩니다!", NamedTextColor.WHITE))
                    )

                    bigTeam = null
                } else {
                    for (pName in bigTeam!!.entries) {
                        val bigTeamEffect = PotionEffect(PotionEffectType.GLOWING, 100, 0)
                        val player = server.getPlayer(pName) ?: continue
                        player.addPotionEffect(bigTeamEffect)
                    }
                }
            } else {
//                logger.info(server.scoreboardManager.mainScoreboard.teams.count().toString())
                for (team in server.scoreboardManager.mainScoreboard.teams){
//                    logger.info(team.entries.count().toString())
                    if (team.entries.count() >= 8){
                        bigTeamOccur = true
                        bigTeam = team
                        val color = NamedTextColor.NAMES.value(team.color.name.lowercase())

                        server.broadcast(
                            Component.text("대세력 발생! 지금부터 ", NamedTextColor.WHITE)
                                .append(Component.text(team.name, color))
                                .append(Component.text("팀은 대세력 디버프를 받게 됩니다!", NamedTextColor.WHITE))
                        )

                        break
                    } else if (team.entries.count() == 0){
                        val color = NamedTextColor.NAMES.value(team.color.name.lowercase())
                        color?.let { unusedTeamColor.add(it) }

                        server.broadcast(
                            Component.text("팀 소멸! ", NamedTextColor.WHITE)
                                .append(Component.text(team.name, color))
                                .append(Component.text("팀이 소멸하였습니다!", NamedTextColor.WHITE))
                        )

                        team.unregister()
                    }
                }
            }
        }, delay, period)

        // pos

        val posPeriod = 36000L // 30분 = 30분 * 60초 * 20틱 = 36000틱
        server.scheduler.runTaskTimer(this, Runnable {
            if (bigTeamOccur) {
                server.broadcast(
                    Component.text("===== ", NamedTextColor.WHITE)
                        .append(Component.text("대세력 ", NamedTextColor.GOLD))
                        .append(Component.text(bigTeam!!.name, NamedTextColor.GOLD))
                        .append(Component.text("팀 좌표공개 ", NamedTextColor.GOLD))
                        .append(Component.text("=====", NamedTextColor.WHITE))
                )
                for (pName in bigTeam!!.entries) {
                    val player = server.getPlayer(pName) ?: continue
                    val playerWorld = player.world.environment
                    var playerWorldName = "(알수없음)";

                    if (playerWorld == World.Environment.NORMAL) playerWorldName = "(오버월드)"
                    if (playerWorld == World.Environment.NETHER) playerWorldName = "(네더)"
                    if (playerWorld == World.Environment.THE_END) playerWorldName = "(엔더)"


                    server.broadcast(
                        Component.text(player.name, NamedTextColor.GREEN)
                            .append(Component.text(playerWorldName, NamedTextColor.GREEN))
                            .append(Component.text(" : ", NamedTextColor.WHITE))
                            .append(Component.text(player.x.toInt(), NamedTextColor.AQUA))
                            .append(Component.text(", ", NamedTextColor.WHITE))
                            .append(Component.text(player.y.toInt(), NamedTextColor.AQUA))
                            .append(Component.text(", ", NamedTextColor.WHITE))
                            .append(Component.text(player.z.toInt(), NamedTextColor.AQUA))
                    )
                }
            }
        }, delay, posPeriod)
    }

    // TODO : file관련 fun 다른 class로 이동
    fun makeFile(f: File) {
        if (!f.exists() || !f.isFile()) {
            try {
                if (!f.parentFile.exists()) {
                    f.parentFile.mkdirs()
                }
                f.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    @Suppress("deprecation")
    fun mapToFileSchedule(f: File?, map: HashMap<UUID, Int>) {
        Bukkit.getScheduler().scheduleAsyncRepeatingTask(this, {
            f?.let { mapToFile(it, map) }
        }, 1200L, 1200L)
    }

    fun fileToMap(f: File, map: HashMap<UUID, Int>) {
        try {
            val reader = BufferedReader(FileReader(f))
            var fileLine: String? = null
            while ((reader.readLine().also { fileLine = it }) != null) {
                val uuid =
                    UUID.fromString(fileLine!!.split("\\|".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0])
                val str = fileLine.split("\\|".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]

                map[uuid] = str.toInt()
            }
        } catch (e3: FileNotFoundException) {
            e3.printStackTrace()
        } catch (e4: IOException) {
            e4.printStackTrace()
        }
    }

    fun mapToFile(f: File, map: HashMap<UUID, Int>) {
        try {
            val writer: FileWriter = FileWriter(f, false)
            for (uuid in map.keys) {
                writer.write(uuid.toString() + "|" + map.get(uuid) + "\n")
            }
            writer.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}