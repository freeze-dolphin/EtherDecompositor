package io.sn.etherdec.modules

import io.sn.etherdec.EtherCore
import io.sn.etherdec.objects.AbstractModule
import org.bukkit.Material
import org.bukkit.entity.Monster
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.inventory.ItemStack
import kotlin.random.Random

class KillAndDrop(plug: EtherCore) : AbstractModule(plug) {

    @EventHandler
    fun onDeath(evt: EntityDeathEvent) {
        val ety = evt.entity
        if (ety is Monster) {
            var value = 0
            value += if (ety.equipment.helmet == null) 0 else 1
            value += if (ety.equipment.chestplate == null) 0 else 1
            value += if (ety.equipment.leggings == null) 0 else 1

            evt.entity.world.dropItemNaturally(evt.entity.location, ItemStack(Material.EMERALD, Random.nextInt(0, value)))
        }
    }

}
