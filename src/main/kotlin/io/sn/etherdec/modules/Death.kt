package io.sn.etherdec.modules

import io.sn.etherdec.EtherCore
import io.sn.etherdec.objects.AListener
import io.sn.etherdec.objects.AbstractModule
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.PlayerDeathEvent


class Death(plug: EtherCore) : AbstractModule(plug), AListener {

    @EventHandler
    fun onDeath(evt: PlayerDeathEvent) {
        evt.player.setBedSpawnLocation(Location(Bukkit.getWorld("world"), 2.5, 60.0, 120.5, 0f, 0f), true)
        evt.deathMessage()?.let { evt.deathMessage(EtherCore.minid("<dark_gray>[<red>喜报<dark_gray>] <white>${EtherCore.plains(it)}")) }
    }

}
