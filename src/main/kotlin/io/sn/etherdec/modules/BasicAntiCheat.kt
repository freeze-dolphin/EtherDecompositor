package io.sn.etherdec.modules

import io.sn.etherdec.EtherCore
import io.sn.etherdec.objects.AListener
import io.sn.etherdec.objects.AbstractModule
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.util.Vector
import kotlin.math.sqrt

class BasicAntiCheat(plug: EtherCore) : AbstractModule(plug), AListener {

    private val sqrt2div2 = sqrt(2.0) / 2.0

    @EventHandler
    fun onClick(evt: PlayerInteractEvent) {
        if (evt.player.hasPermission("themis.bypass")) return

        if (evt.hasBlock()) {
            val blk = evt.clickedBlock
            val point2 = blk!!.location.toCenterLocation()
            val point1 = evt.player.eyeLocation

            val space = 0.5
            val distance: Double = point1.distance(point2)
            val p1: Vector = point1.toVector()
            val p2: Vector = point2.toVector()
            val vector: Vector = p2.clone().subtract(p1).normalize().multiply(space)
            var length = 0.0
            while (length < distance) {
                val cblk = p1.toLocation(evt.player.world).block
                if (cblk.isSolid && p1.distance(p2) > sqrt2div2) {
                    evt.setUseInteractedBlock(Event.Result.DENY)
                    return
                }
                length += space
                p1.add(vector)
            }
        }
    }

    @EventHandler
    fun onJoin(evt: PlayerJoinEvent) {
        if (evt.player.hasPermission("themis.bypass")) return
        if (evt.player.ping > plug.config.getInt("anti-cheat.ping-limitation")) {
            evt.player.kick(EtherCore.minid("<red>你的延迟过高，不允许进服"))
        }
    }

}
