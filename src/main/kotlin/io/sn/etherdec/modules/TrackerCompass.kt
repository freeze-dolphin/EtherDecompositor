package io.sn.etherdec.modules

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType
import io.github.thebusybiscuit.slimefun4.core.attributes.Rechargeable
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler
import io.github.thebusybiscuit.slimefun4.utils.LoreBuilder
import io.sn.etherdec.EtherCore
import io.sn.etherdec.findLastNewIndex
import io.sn.etherdec.objects.AListener
import io.sn.etherdec.objects.AbstractModule
import io.sn.etherdec.vecAscend
import org.bukkit.*
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.ItemStack
import java.util.*
import kotlin.io.path.Path
import kotlin.math.sqrt

@Deprecated("没用罢了")
class TrackerCompass(plug: EtherCore) : AbstractModule(plug), AListener {

    private val stack = SlimefunItemStack(
        "ETHERITE_TRACKER_COMPASS",
        Material.COMPASS,
        "&e追踪器&r",
        "",
        "&e右键 &f进行追踪",
        "&eShift + 右键 &f更改模式",
        "",
        LoreBuilder.powerCharged(0, 128)
    )

    override fun postSetup() {
        AutoTracker(
            plug, stack, type, nullRecipe
        )
        // .register(plug)
    }

    class AutoTracker(private val plug: EtherCore, item: SlimefunItemStack, type: RecipeType, recipe: Array<ItemStack?>) :
        SlimefunItem(plug.group, item, type, recipe), Rechargeable {

        private val selectedMode: MutableMap<UUID, Int> = mutableMapOf()
        private val modes: List<TrackerMode> = listOf(TrackerMode.CHEST, TrackerMode.PLAYER)

        private fun nextIndex(i: Int): Int = if (i == 0) 1 else 0

        override fun preRegister() {
            addItemHandler(ItemUseHandler {
                it.cancel()

                val curMode = selectedMode.getOrDefault(it.player.uniqueId, 0)

                if (it.player.isSneaking) {
                    val ccMode = nextIndex(curMode)
                    selectedMode[it.player.uniqueId] = ccMode
                    plug.sendMsg(it.player, "<white>切换到模式: <yellow>${modes[ccMode].modeName}")
                } else {
                    when (modes[selectedMode.getOrDefault(it.player.uniqueId, 0)]) {
                        TrackerMode.PLAYER -> {
                            getClosestPlayer(it.player.location, it.player.uniqueId).let { plr ->
                                if (plr != null) {
                                    val targetLoc = Bukkit.getPlayer(plr)?.eyeLocation
                                    if (targetLoc?.world?.name == it.player.world.name) {
                                        if (removeItemCharge(it.item, getCostPerUse())) {
                                            it.player.compassTarget = targetLoc
                                            vecAscend(
                                                plug,
                                                it.player.eyeLocation,
                                                targetLoc,
                                                0.5,
                                                plug.config.getLong("tracker.particle-interval", 100L)
                                            ) { vec ->
                                                val spl = vec.toLocation(it.player.world)
                                                it.player.spawnParticle(Particle.END_ROD, spl, 1, 0.0, 0.0, 0.0, 0.001)
                                            }
                                        } else {
                                            it.player.sendActionBar(EtherCore.minid("<red>电量不足"))
                                            it.player.playSound(it.player, Sound.ENTITY_ITEM_BREAK, 1f, 1f)
                                        }
                                    }
                                } else {
                                    plug.sendMsg(it.player, "<white>找不到可追踪的对象")
                                }
                            }
                        }

                        TrackerMode.CHEST -> {
                            getClosestChest(it.player.location).let { loc ->
                                if (loc != null) {
                                    val targetLoc = loc.toCenterLocation()
                                    if (targetLoc.world?.name == it.player.world.name) {
                                        if (removeItemCharge(it.item, getCostPerUse())) {
                                            it.player.compassTarget = targetLoc
                                            vecAscend(
                                                plug,
                                                it.player.eyeLocation,
                                                targetLoc,
                                                0.5,
                                                plug.config.getLong("tracker.particle-interval", 100L)
                                            ) { vec ->
                                                val spl = vec.toLocation(it.player.world)
                                                it.player.spawnParticle(Particle.END_ROD, spl, 1, 0.0, 0.0, 0.0, 0.001)
                                            }
                                        } else {
                                            it.player.sendActionBar(EtherCore.minid("<red>电量不足"))
                                            it.player.playSound(it.player, Sound.ENTITY_ITEM_BREAK, 1f, 1f)
                                        }
                                    }
                                } else {
                                    plug.sendMsg(it.player, "<white>找不到可追踪的对象")
                                }
                            }
                        }
                    }
                }
            })
        }

        private fun getCostPerUse(): Float = 0.5F

        override fun getMaxItemCharge(item: ItemStack?): Float = 128F

        private fun getClosestPlayer(loc: Location, exceptPlayerId: UUID): UUID? {
            if (loc.world.name !in plug.config.getStringList("tracker.applied-worlds")) return null

            var closestPlayer: UUID? = null
            var distanceToClosestPlayer = 0.0
            val xLoc: Double = loc.x
            val yLoc: Double = loc.y
            for (player in plug.server.onlinePlayers) {
                if (player.uniqueId !== exceptPlayerId && player.gameMode == GameMode.ADVENTURE) {
                    val p2xLoc = player.location.x
                    val p2yLoc = player.location.y
                    val distance = sqrt((p2yLoc - yLoc) * (p2yLoc - yLoc) + (p2xLoc - xLoc) * (p2xLoc - xLoc))
                    if (closestPlayer == null) {
                        distanceToClosestPlayer = distance
                        closestPlayer = player.uniqueId
                    } else {
                        if (distance < distanceToClosestPlayer) {
                            distanceToClosestPlayer = distance
                            closestPlayer = player.uniqueId
                        }
                    }
                }
            }
            return closestPlayer
        }

        private fun getClosestChest(loc: Location): Location? {
            if (loc.world.name !in plug.config.getStringList("tracker.applied-worlds")) return null

            val f = Path(plug.dataFolder.path, "locations.yml").toFile()
            if (f.exists()) {
                YamlConfiguration.loadConfiguration(f).apply {
                    if (!contains("${loc.world.name}.0")) return null

                    val sec = getConfigurationSection(loc.world.name)
                    val lstIdx = findLastNewIndex(0, sec!!) - 1
                    return (0..lstIdx).map { cidx ->
                        getLocation("${loc.world.name}.$cidx")
                    }.minByOrNull {
                        it?.distance(loc) ?: 5000.0
                    }
                }
            }

            return null
        }

    }

    enum class TrackerMode(val modeName: String) {
        PLAYER("追踪玩家"), CHEST("追踪补给箱");
    }

}
