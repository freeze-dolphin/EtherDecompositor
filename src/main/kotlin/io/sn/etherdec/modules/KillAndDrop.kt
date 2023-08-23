package io.sn.etherdec.modules

import io.sn.etherdec.EtherCore
import io.sn.etherdec.objects.AListener
import io.sn.etherdec.objects.AbstractModule
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.Monster
import org.bukkit.entity.Zombie
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.inventory.ItemStack
import kotlin.random.Random

class KillAndDrop(plug: EtherCore) : AbstractModule(plug), AListener {

    @EventHandler
    fun onDeath(evt: EntityDeathEvent) {
        val ety = evt.entity
        if (ety is Monster) {
            var value = 1
            value += if (ety.equipment.chestplate == null) 0 else 2
            value += if (ety.equipment.leggings == null) 0 else 2

            if (ety.type == EntityType.PHANTOM) {
                evt.entity.world.dropItemNaturally(evt.entity.location, ItemStack(Material.EMERALD, Random.nextInt(5, 9)))
            }

            if (Random.nextDouble() < if (ety is Zombie && !ety.isAdult) 0.9 else 0.6) {
                evt.entity.world.dropItemNaturally(evt.entity.location, ItemStack(Material.EMERALD, Random.nextInt(0, value)))
            }
        }
    }

}
