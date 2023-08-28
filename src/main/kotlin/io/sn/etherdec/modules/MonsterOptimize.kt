package io.sn.etherdec.modules

import io.sn.etherdec.EtherCore
import io.sn.etherdec.objects.AListener
import io.sn.etherdec.objects.AbstractModule
import me.deecaad.weaponmechanics.WeaponMechanics
import org.bukkit.Location
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
        if (evt.location.world.name != "chernobyl") return
        if (evt.entity.shooter is Skeleton) {
            if (evt.entity is Arrow) {
                val arr = evt.entity as Arrow
                if (Random.nextDouble() < 0.4) arr.addCustomEffect(PotionEffect(PotionEffectType.POISON, Random.nextInt(80, 120), 1), true)
            }
        }
    }

    private fun checkForSpace(loc: Location): Boolean = (0..plug.config.getInt("mob-spawning.required-space")).map {
        Location(loc.world, loc.x, loc.y + it, loc.z)
    }.all {
        it.block.type.name.contains(Regex(".*(AIR|LEAVE|COBWEB|WATER|LAVA).*"))
    }

    @EventHandler
    fun onSpawn(evt: EntitySpawnEvent) {
        if (evt.entity.entitySpawnReason == CreatureSpawnEvent.SpawnReason.NATURAL && evt.entity is Monster) {
            evt.isCancelled = true

            if (evt.location.world.name !in plug.config.getStringList("mob-spawning.applied-worlds")) return // disable mob spawn except enabled worlds`

            // if (evt.location.world.isDayTime) return // spawning monsters in daytime is not allowed

            if (!checkForSpace(evt.location)) return // not enough space

            if (Random.nextDouble() > plug.config.getDouble("mob-spawning.monster-spawn-rate", 0.5)) return

            val etype: EntityType
            val rnd = Random.nextDouble()

            val underType = evt.location.apply {
                y -= 1.0
            }.block.type.name

            if (rnd < plug.config.getDouble("mob-spawning.skeleton-spawn-rate", 0.005)) {
                etype = if (underType.contains(Regex(".*ICE.*"))) {
                    EntityType.STRAY
                } else {
                    EntityType.SKELETON
                }
            } else if (rnd < plug.config.getDouble("mob-spawning.phantom-spawn-rate", 0.08)) {
                etype = EntityType.PHANTOM
                if (evt.location.world.isDayTime) return
            } else {
                if (Random.nextDouble() < plug.config.getDouble("mob-spawning.zombie-spawn-decreaser-rate", 0.5)) return

                etype = if (underType.contains(Regex(".*SAND.*"))) {
                    EntityType.HUSK
                } else if (evt.location.block.type.name.contains(Regex(".*WATER.*"))) {
                    EntityType.DROWNED
                } else {
                    EntityType.ZOMBIE
                }
            }
            evt.entity.world.spawnEntity(evt.location.apply {
                if (etype == EntityType.PHANTOM) {
                    y += 20.0
                }
            }, etype, CreatureSpawnEvent.SpawnReason.DEFAULT) {
                val ety = it as Mob
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
                if (ety.equipment.helmet.type == Material.AIR && ety.equipment.chestplate.type == Material.AIR && ety.equipment.leggings.type == Material.AIR && ety.equipment.boots.type == Material.AIR) {
                    if (Random.nextDouble() < plug.config.getDouble("mob-spawning.assassin-zombie-spawn-rate", 0.1)) {
                        ety.equipment.setItemInMainHand(ItemStack(Material.DIAMOND_SWORD))
                        if (Random.nextDouble() < plug.config.getDouble("mob-spawning.assassin-zombie-speed-rate", 0.5)) {
                            ety.addPotionEffect(PotionEffect(PotionEffectType.SPEED, 3600, Random.nextInt(1, 2)))
                        }
                    }
                }

                if (ety is Phantom) {
                    ety.target =
                        ety.location.getNearbyPlayers(plug.config.getDouble("mob-spawning.phantom-view-radius", 50.0)).let { nearBy ->
                            if (nearBy.isEmpty()) return@spawnEntity
                            nearBy.elementAt(0)
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
