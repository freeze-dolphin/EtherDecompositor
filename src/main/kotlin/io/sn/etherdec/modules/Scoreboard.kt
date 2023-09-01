@file:Suppress("unused", "ControlFlowWithEmptyBody")

package io.sn.etherdec.modules

import io.sn.etherdec.*
import io.sn.etherdec.objects.*
import org.bukkit.Statistic
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.PlayerDeathEvent
import java.util.*
import kotlin.io.path.Path

@Suppress("UNUSED_ANONYMOUS_PARAMETER")
@Deprecated("换用其他插件了，源于对自己代码的不自信")
class Scoreboard(plug: EtherCore) : AbstractModule(plug), AListener, Unregisterable, Reloadable {

    private val statFile = Path(plug.dataFolder.path, "statistics.yml").toFile()
    private val statistics = YamlConfiguration.loadConfiguration(statFile)

    enum class TopItemType {
        KILLS, DEATHS;

        val internalName = name.lowercase()
    }

    private fun getTop(@Suppress("SameParameterValue") type: TopItemType): List<Pair<UUID, Int>> =
        statistics.getStringList("top-${type.internalName}").map { deser(it) }.sortedByDescending {
            it.second
        }

    private fun setTop(type: TopItemType, l: List<Pair<UUID, Int>>) {
        statistics.set("top-${type.internalName}") {
            l.map {
                ser(it.first, it.second)
            }
        }
    }

    private fun deser(s: String): Pair<UUID, Int> = s.split(";").let {
        Pair(UUID.fromString(it[0]), it[1].toInt())
    }

    private fun ser(uid: UUID, num: Int) = "$uid;$num"

    override fun preSetup() {
        if (!statistics.contains("inited")) {
            statistics.set("inited", true)
            statistics.set("top-kills", listOf<String>())
            statistics.set("top-deaths", listOf<String>())
            statistics.save(statFile)
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onKill(evt: PlayerDeathEvent) {
        var changed = false

        // ** handle death **

        val curDeath = evt.entity.getStatistic(Statistic.DEATHS) // 注意考虑 Bukkit 内部对死亡计数的修改，因此可能会受到 EventPriority 影响
        val curTopDeaths = getTop(TopItemType.DEATHS)

        if (curTopDeaths.last().second < curDeath) { // 检查是否有必要去更新
            changed = true

            // 检查排行榜中是否已经存在该玩家
            curTopDeaths.forEachIndexed { idx, stat ->
                val uid = stat.first
                if (uid == evt.entity.uniqueId) {
                    // 存在，更新排行榜
                }
            }
        }

        if (evt.entity.killer is Player) {
            // ** handle kill **


        }

        if (changed) statistics.save(statFile)
    }

    override fun postSetup() {
        scheduleTimer(plug, plug.config.getLong("scoreboard.update-interval", 60)) {
            plug.server.onlinePlayers.forEach { plr ->
                @Suppress("DEPRECATION")
                when (getSettingForPlayer<ScoreBoardStatus>(CommonSettings.scoreboardKey, plr.uniqueId)) {
                    ScoreBoardStatus.DISABLED -> EtherCore.board.deleteBoard(plr)
                    ScoreBoardStatus.BY_KILL -> EtherCore.board.createBoard(plr, legacyfmt("&e排行榜 &7- &c击杀数")).apply {


                        arrayOf("").zip(
                            arrayOf(1)
                        ).forEach {
                            this.set(legacyfmt(it.first), it.second)
                        }
                    }

                    ScoreBoardStatus.BY_DEATH -> {}
                }
            }
        }
    }

    override fun onDisable() {
        statistics.save(statFile)
    }

    override fun onReload() {
        statistics.load(statFile)
    }

}
