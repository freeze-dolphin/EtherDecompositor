package io.sn.etherdec.modules

import io.sn.etherdec.EtherCore
import io.sn.etherdec.objects.AListener
import io.sn.etherdec.objects.AbstractModule
import org.bukkit.Bukkit
import org.bukkit.Particle
import org.bukkit.attribute.Attribute
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageEvent
import kotlin.math.max

class Bleeding(plug: EtherCore) : AbstractModule(plug), AListener {

    override fun postSetup() {
        Bukkit.getScheduler().runTaskTimer(plug, Runnable {
            plug.server.onlinePlayers.forEach {
                val ratio = it.health / it.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.baseValue
                it.freezeTicks = max(0, ((1 - ratio) * it.maxFreezeTicks).toInt())
            }
        }, 0L, 1L)
    }

    @EventHandler
    fun onAttack(evt: EntityDamageEvent) {
        if (evt.entity is LivingEntity) {
            val ety = evt.entity as LivingEntity
            evt.entity.world.spawnParticle(Particle.DAMAGE_INDICATOR, ety.eyeLocation, (evt.damage / 2).toInt(), 0.2, 0.2, 0.2, 0.02)
        }
    }

}