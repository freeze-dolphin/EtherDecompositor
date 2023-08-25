package io.sn.etherdec.modules

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile
import io.github.thebusybiscuit.slimefun4.core.attributes.ProtectionType
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun
import io.github.thebusybiscuit.slimefun4.utils.itemstack.ItemStackWrapper
import io.sn.etherdec.EtherCore
import io.sn.etherdec.objects.AListener
import io.sn.etherdec.objects.AbstractModule
import io.sn.etherdec.scheduleTimer
import org.bukkit.Material
import org.bukkit.entity.Mob
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class Chernobyl(plug: EtherCore) : AbstractModule(plug), AListener {

    override fun postSetup() {
        scheduleTimer(plug, plug.config.getLong("chernobyl.rain-check-interval", 20L)) {
            val wrd = plug.server.getWorld("chernobyl")

            if (wrd?.isClearWeather == true) return@scheduleTimer

            wrd?.players?.forEach { p ->
                PlayerProfile.get(p) { profile: PlayerProfile ->
                    checkForRadiation(p, profile)
                }
            }
        }

        scheduleTimer(plug, plug.config.getLong("chernobyl.water-check-interval", 20L)) {
            val wrd = plug.server.getWorld("chernobyl")

            wrd?.players?.forEach { p ->
                if (p.location.block.type == Material.WATER) {
                    PlayerProfile.get(p) { profile: PlayerProfile ->
                        checkForRadiation(p, profile)
                    }
                }
            }
        }
    }

    @EventHandler
    fun onAttack(evt: EntityDamageByEntityEvent) {
        if (evt.entity.location.world.name != "chernobyl") return

        if (evt.entity is Player && evt.damager is Mob) {
            val p = evt.entity as Player

            PlayerProfile.get(p) { profile: PlayerProfile ->
                checkForRadiation(p, profile)
            }
        }
    }

    private fun checkForRadiation(p: Player, profile: PlayerProfile) {
        if (!profile.hasFullProtectionAgainst(ProtectionType.RADIATION)) {
            for (item in p.inventory) {
                if (checkAndApplyRadiation(p, item)) {
                    break
                }
            }
        }
    }

    private fun checkAndApplyRadiation(p: Player, item: ItemStack?): Boolean {
        if (item == null || item.type == Material.AIR) {
            return false
        }
        val radioactiveItems = Slimefun.getRegistry().radioactiveItems
        var itemStack = item
        if (item !is SlimefunItemStack && radioactiveItems.size > 1) {
            itemStack = ItemStackWrapper.wrap(item)
        }
        for (radioactiveItem in radioactiveItems) {
            if (radioactiveItem.isItem(itemStack) && !radioactiveItem.isDisabledIn(p.world)) {
                Slimefun.runSync {
                    p.addPotionEffects(
                        setOf(
                            PotionEffect(PotionEffectType.WITHER, 400, 2),
                            PotionEffect(PotionEffectType.BLINDNESS, 400, 3),
                            PotionEffect(PotionEffectType.CONFUSION, 400, 3),
                            PotionEffect(PotionEffectType.WEAKNESS, 400, 2),
                            PotionEffect(PotionEffectType.SLOW, 400, 1),
                            PotionEffect(PotionEffectType.SLOW_DIGGING, 400, 1)
                        )
                    )
                    p.fireTicks = 400
                }
                return true
            }
        }
        return false
    }

}
