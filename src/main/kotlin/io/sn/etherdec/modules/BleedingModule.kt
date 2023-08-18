package io.sn.etherdec.modules

import io.sn.etherdec.EtherCore
import io.sn.etherdec.objects.AbstractModule
import org.bukkit.Bukkit
import org.bukkit.attribute.Attribute

class BleedingModule(plug: EtherCore) : AbstractModule(plug) {

    override fun postSetup() {
        Bukkit.getScheduler().runTaskTimer(plug, Runnable {
            plug.server.onlinePlayers.forEach {
                val ratio = it.health / it.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.baseValue
                it.freezeTicks = ((1 - ratio) * it.maxFreezeTicks).toInt()
            }
        }, 0L, 1L)
    }

}