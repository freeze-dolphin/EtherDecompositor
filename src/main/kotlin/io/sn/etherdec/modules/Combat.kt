package io.sn.etherdec.modules

import io.sn.etherdec.EtherCore
import io.sn.etherdec.objects.AListener
import io.sn.etherdec.objects.AbstractModule
import net.kyori.adventure.bossbar.BossBar
import org.bukkit.Bukkit
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageByEntityEvent

class Combat(plug: EtherCore) : AbstractModule(plug), AListener {


    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onHit(evt: EntityDamageByEntityEvent) {
        if (evt.entity is Player && evt.damager is Player) {
            val plr = evt.entity as Player
            val dmg = evt.damage

            BossBar.bossBar(
                EtherCore.minid("<yellow>${plr.name} <gray>| <dark_red>- <red>$dmg"),
                (plr.health / plr.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.value).toFloat(),
                BossBar.Color.RED,
                BossBar.Overlay.PROGRESS
            ).let {
                it.addViewer(evt.damager)
                Bukkit.getScheduler().runTaskLater(plug, Runnable {
                    try {
                        it.removeViewer(evt.damager)
                    } catch (_: Exception) {
                    }
                }, plug.config.getLong("display.health-bar-show-time", 40L))
            }

        }
    }

}
