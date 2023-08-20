package io.sn.etherdec.modules

import io.sn.etherdec.EtherCore
import io.sn.etherdec.objects.AListener
import io.sn.etherdec.objects.AbstractModule
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Monster
import org.bukkit.entity.Zombie
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.event.entity.EntitySpawnEvent
import org.bukkit.inventory.ItemStack
import kotlin.random.Random

class MonsterOptimize(plug: EtherCore) : AbstractModule(plug), AListener {

    @EventHandler
    fun onSpawn(evt: EntitySpawnEvent) {
        if (evt.entity.entitySpawnReason == CreatureSpawnEvent.SpawnReason.NATURAL && evt.entity is Monster && evt.entityType != EntityType.ZOMBIE) {
            evt.isCancelled = true
            evt.entity.world.spawnEntity(evt.location, EntityType.ZOMBIE, CreatureSpawnEvent.SpawnReason.DEFAULT) {
                val ety = it as Zombie
                Random.nextInt(1, 200).let { rnd ->
                    when (rnd) {
                        in 1 until 50 -> equipEntityWith(ety, Material.LEATHER)
                        in 50 until 75 -> equipEntityWith(ety, Material.GOLD_INGOT)
                        in 75 until 80 -> equipEntityWith(ety, Material.CHAIN)
                        in 80 until 95 -> equipEntityWith(ety, Material.IRON_INGOT)
                        in 95..100 -> equipEntityWith(ety, Material.DIAMOND)
                        else -> return@let
                    }
                }
            }
        }
    }

    private fun chanceWithItemStack(item: ItemStack): ItemStack? {
        return if (Random.nextBoolean()) item else null
    }

    private fun equipEntityWith(ety: LivingEntity, material: Material) {
        when (material) {
            Material.DIAMOND -> {
                ety.equipment?.helmetDropChance = 0.05F
                ety.equipment?.chestplateDropChance = 0.05F
                ety.equipment?.leggingsDropChance = 0.05F
                ety.equipment?.bootsDropChance = 0.05F

                ety.equipment?.helmet = chanceWithItemStack(ItemStack(Material.DIAMOND_HELMET))
                ety.equipment?.chestplate = chanceWithItemStack(ItemStack(Material.DIAMOND_CHESTPLATE))
                ety.equipment?.leggings = chanceWithItemStack(ItemStack(Material.DIAMOND_LEGGINGS))
                ety.equipment?.boots = chanceWithItemStack(ItemStack(Material.DIAMOND_BOOTS))
            }

            Material.IRON_INGOT -> {
                ety.equipment?.helmetDropChance = 0.05F
                ety.equipment?.chestplateDropChance = 0.05F
                ety.equipment?.leggingsDropChance = 0.05F
                ety.equipment?.bootsDropChance = 0.05F

                ety.equipment?.helmet = chanceWithItemStack(ItemStack(Material.IRON_HELMET))
                ety.equipment?.chestplate = chanceWithItemStack(ItemStack(Material.IRON_CHESTPLATE))
                ety.equipment?.leggings = chanceWithItemStack(ItemStack(Material.IRON_LEGGINGS))
                ety.equipment?.boots = chanceWithItemStack(ItemStack(Material.IRON_BOOTS))
            }

            Material.GOLD_INGOT -> {
                ety.equipment?.helmetDropChance = 0.05F
                ety.equipment?.chestplateDropChance = 0.05F
                ety.equipment?.leggingsDropChance = 0.05F
                ety.equipment?.bootsDropChance = 0.05F

                ety.equipment?.helmet = chanceWithItemStack(ItemStack(Material.GOLDEN_HELMET))
                ety.equipment?.chestplate = chanceWithItemStack(ItemStack(Material.GOLDEN_CHESTPLATE))
                ety.equipment?.leggings = chanceWithItemStack(ItemStack(Material.GOLDEN_LEGGINGS))
                ety.equipment?.boots = chanceWithItemStack(ItemStack(Material.GOLDEN_BOOTS))
            }

            Material.CHAIN -> {
                ety.equipment?.helmetDropChance = 0.05F
                ety.equipment?.chestplateDropChance = 0.05F
                ety.equipment?.leggingsDropChance = 0.05F
                ety.equipment?.bootsDropChance = 0.05F

                ety.equipment?.helmet = chanceWithItemStack(ItemStack(Material.CHAINMAIL_HELMET))
                ety.equipment?.chestplate = chanceWithItemStack(ItemStack(Material.CHAINMAIL_CHESTPLATE))
                ety.equipment?.leggings = chanceWithItemStack(ItemStack(Material.CHAINMAIL_LEGGINGS))
                ety.equipment?.boots = chanceWithItemStack(ItemStack(Material.CHAINMAIL_BOOTS))
            }

            Material.LEATHER -> {
                ety.equipment?.helmetDropChance = 0.05F
                ety.equipment?.chestplateDropChance = 0.05F
                ety.equipment?.leggingsDropChance = 0.05F
                ety.equipment?.bootsDropChance = 0.05F

                ety.equipment?.helmet = chanceWithItemStack(ItemStack(Material.LEATHER_HELMET))
                ety.equipment?.chestplate = chanceWithItemStack(ItemStack(Material.LEATHER_CHESTPLATE))
                ety.equipment?.leggings = chanceWithItemStack(ItemStack(Material.LEATHER_LEGGINGS))
                ety.equipment?.boots = chanceWithItemStack(ItemStack(Material.LEATHER_BOOTS))
            }

            else -> {}
        }
    }

}
