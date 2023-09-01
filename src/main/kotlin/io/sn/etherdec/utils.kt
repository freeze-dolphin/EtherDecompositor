@file:Suppress("DEPRECATION")

package io.sn.etherdec


import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import java.util.*
import java.util.concurrent.TimeUnit
import de.cubbossa.commonsettings.NamespacedKey as SettingKey


fun vecAscend(plug: EtherCore, point1: Location, point2: Location, space: Double, delay: Long = 0L, func: (Vector) -> Unit) {
    val distance: Double = point1.distance(point2)
    val p1: Vector = point1.toVector()
    val p2: Vector = point2.toVector()
    val vector: Vector = p2.clone().subtract(p1).normalize().multiply(space)
    var length = 0.0
    while (length < distance) {
        Bukkit.getAsyncScheduler().runDelayed(plug, {
            func.invoke(p1)
            length += space
            p1.add(vector)
        }, delay, TimeUnit.MILLISECONDS)
    }
}

fun scheduleTimer(plug: EtherCore, period: Long, runnable: Runnable) = Bukkit.getScheduler().runTaskTimer(plug, runnable, 0L, period)

tailrec fun findLastNewIndex(current: Int, yml: ConfigurationSection): Int = if (yml.contains(current.toString())) {
    findLastNewIndex(current + 1, yml)
} else {
    current
}

fun getPermVariable(plr: Player, startWith: String, default: Int): Int {
    return plr.effectivePermissions.filter {
        it.permission.startsWith(startWith)
    }.maxOfOrNull {
        it.permission.split(".").last().toInt()
    } ?: default
}

fun <T> getSettingForPlayer(key: SettingKey, uid: UUID): T = EtherCore.settings.getSetting<T>(key).getValue(uid)

fun ph(originalText: String, plr: Player): String = PlaceholderAPI.setPlaceholders(plr, originalText)

fun legacyfmt(text: String): String = ChatColor.translateAlternateColorCodes('&', text)
