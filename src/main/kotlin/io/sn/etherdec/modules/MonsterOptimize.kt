package io.sn.etherdec.modules

import io.sn.etherdec.EtherCore
import io.sn.etherdec.objects.AListener
import io.sn.etherdec.objects.AbstractModule
import me.deecaad.weaponmechanics.WeaponMechanics
import org.bukkit.Material
import org.bukkit.entity.*
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.event.entity.EntitySpawnEvent
import org.bukkit.event.entity.ProjectileLaunchEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import kotlin.random.Random

class MonsterOptimize(plug: EtherCore) : AbstractModule(plug), AListener {

    @EventHandler
    fun onShoot(evt: ProjectileLaunchEvent) {
        if (evt.entity.shooter is Skeleton) {
            if (evt.entity is Arrow) {
                val arr = evt.entity as Arrow
                arr.damage = Random.nextDouble(3.0, 6.0)
                if (Random.nextDouble() < 0.4) arr.addCustomEffect(PotionEffect(PotionEffectType.POISON, Random.nextInt(80, 120), 1), true)
            }
        }
    }

    @EventHandler
    fun onSpawn(evt: EntitySpawnEvent) {
        if (evt.entity.entitySpawnReason == CreatureSpawnEvent.SpawnReason.NATURAL && evt.entity is Monster && evt.entityType != EntityType.ZOMBIE) {
            evt.isCancelled = true
            if (Random.nextDouble() > 0.05) {
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
            } else {
                evt.entity.world.spawnEntity(evt.location, EntityType.SKELETON, CreatureSpawnEvent.SpawnReason.DEFAULT) {
                    val ety = it as Skeleton
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
    }

    private fun chanceWithItemStack(item: ItemStack): ItemStack? {
        return if (Random.nextBoolean()) item else null
    }

    private fun equipEntityWith(ety: Mob, material: Material) {
        when (material) {
            Material.DIAMOND -> {
                ety.equipment.helmetDropChance = 0.05F
                ety.equipment.chestplateDropChance = 0.05F
                ety.equipment.leggingsDropChance = 0.05F
                ety.equipment.bootsDropChance = 0.05F

                ety.equipment.helmet = chanceWithItemStack(ItemStack(Material.DIAMOND_HELMET))
                ety.equipment.chestplate = chanceWithItemStack(ItemStack(Material.DIAMOND_CHESTPLATE))
                ety.equipment.leggings = chanceWithItemStack(ItemStack(Material.DIAMOND_LEGGINGS))
                ety.equipment.boots = chanceWithItemStack(ItemStack(Material.DIAMOND_BOOTS))

                if (ety.equipment.helmet.type == Material.DIAMOND_HELMET && ety.equipment.chestplate.type == Material.DIAMOND_CHESTPLATE && ety.equipment.leggings.type == Material.DIAMOND_LEGGINGS && ety.equipment.boots.type == Material.DIAMOND_BOOTS) {
                    if (Random.nextBoolean()) {
                        ety.equipment.setItemInMainHand(ItemStack(Material.DIAMOND_SWORD))
                        ety.equipment.itemInMainHandDropChance = 0.05F
                    }
                }
            }

            Material.IRON_INGOT -> {
                ety.equipment.helmetDropChance = 0.05F
                ety.equipment.chestplateDropChance = 0.05F
                ety.equipment.leggingsDropChance = 0.05F
                ety.equipment.bootsDropChance = 0.05F

                ety.equipment.helmet = chanceWithItemStack(ItemStack(Material.IRON_HELMET))
                ety.equipment.chestplate = chanceWithItemStack(ItemStack(Material.IRON_CHESTPLATE))
                ety.equipment.leggings = chanceWithItemStack(ItemStack(Material.IRON_LEGGINGS))
                ety.equipment.boots = chanceWithItemStack(ItemStack(Material.IRON_BOOTS))

                if (ety.equipment.helmet.type == Material.IRON_HELMET && ety.equipment.chestplate.type == Material.IRON_CHESTPLATE && ety.equipment.leggings.type == Material.IRON_LEGGINGS && ety.equipment.boots.type == Material.IRON_BOOTS) {
                    if (Random.nextBoolean()) {
                        ety.equipment.setItemInMainHand(ItemStack(Material.IRON_SWORD))
                        ety.equipment.itemInMainHandDropChance = 0.05F
                    }
                }
            }

            Material.GOLD_INGOT -> {
                ety.equipment.helmetDropChance = 0.05F
                ety.equipment.chestplateDropChance = 0.05F
                ety.equipment.leggingsDropChance = 0.05F
                ety.equipment.bootsDropChance = 0.05F

                ety.equipment.helmet = chanceWithItemStack(ItemStack(Material.GOLDEN_HELMET))
                ety.equipment.chestplate = chanceWithItemStack(ItemStack(Material.GOLDEN_CHESTPLATE))
                ety.equipment.leggings = chanceWithItemStack(ItemStack(Material.GOLDEN_LEGGINGS))
                ety.equipment.boots = chanceWithItemStack(ItemStack(Material.GOLDEN_BOOTS))

                if (ety.equipment.helmet.type == Material.GOLDEN_HELMET && ety.equipment.chestplate.type == Material.GOLDEN_CHESTPLATE && ety.equipment.leggings.type == Material.GOLDEN_LEGGINGS && ety.equipment.boots.type == Material.GOLDEN_BOOTS) {
                    if (Random.nextBoolean()) {
                        ety.equipment.setItemInMainHand(WeaponMechanics.getWeaponHandler().infoHandler.generateWeapon("Combat_Knife", 1))
                        ety.equipment.itemInMainHandDropChance = 0F
                    } else {
                        ety.equipment.setItemInMainHand(ItemStack(Material.IRON_SWORD))
                        ety.equipment.itemInMainHandDropChance = 0.05F
                    }
                }
            }

            Material.CHAIN -> {
                ety.equipment.helmetDropChance = 0.05F
                ety.equipment.chestplateDropChance = 0.05F
                ety.equipment.leggingsDropChance = 0.05F
                ety.equipment.bootsDropChance = 0.05F

                ety.equipment.helmet = chanceWithItemStack(ItemStack(Material.CHAINMAIL_HELMET))
                ety.equipment.chestplate = chanceWithItemStack(ItemStack(Material.CHAINMAIL_CHESTPLATE))
                ety.equipment.leggings = chanceWithItemStack(ItemStack(Material.CHAINMAIL_LEGGINGS))
                ety.equipment.boots = chanceWithItemStack(ItemStack(Material.CHAINMAIL_BOOTS))
            }

            Material.LEATHER -> {
                ety.equipment.helmetDropChance = 0.05F
                ety.equipment.chestplateDropChance = 0.05F
                ety.equipment.leggingsDropChance = 0.05F
                ety.equipment.bootsDropChance = 0.05F

                ety.equipment.helmet = chanceWithItemStack(ItemStack(Material.LEATHER_HELMET))
                ety.equipment.chestplate = chanceWithItemStack(ItemStack(Material.LEATHER_CHESTPLATE))
                ety.equipment.leggings = chanceWithItemStack(ItemStack(Material.LEATHER_LEGGINGS))
                ety.equipment.boots = chanceWithItemStack(ItemStack(Material.LEATHER_BOOTS))
            }

            else -> {}
        }
    }

}
