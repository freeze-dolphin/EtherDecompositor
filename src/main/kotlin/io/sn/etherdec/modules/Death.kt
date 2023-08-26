package io.sn.etherdec.modules

import com.spawnchunk.emeraldbank.modules.Balance
import io.sn.etherdec.EtherCore
import io.sn.etherdec.objects.AListener
import io.sn.etherdec.objects.AbstractModule
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.PlayerDeathEvent
import kotlin.math.min
import kotlin.random.Random

class Death(plug: EtherCore) : AbstractModule(plug), AListener {

    @EventHandler
    fun onDeath(evt: PlayerDeathEvent) {
        evt.player.setBedSpawnLocation(Location(Bukkit.getWorld("world"), 2.5, 60.0, 120.5, 0f, 0f), true)
        evt.deathMessage()?.let {
            evt.deathMessage(
                EtherCore.minid(
                    "<dark_gray>[<red>喜报<dark_gray>] <reset><white>"
                ).append(it.replaceText { txt ->
                    txt.match(evt.player.name).replacement(" ${evt.player.name} ")
                })
            )
        }

        evt.keepLevel = true
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onKilled(evt: EntityDamageByEntityEvent) {
        if (evt.entity is Player) {
            val player = evt.entity as Player
            if (evt.finalDamage >= player.health) {

                if (player.world.name == "world") return

                val rnd = Random.nextInt(10, 32).toDouble()

                val acc = Balance.balance(player)
                val droped = min(rnd, acc)
                if (droped < 1) return
                Balance.remove(player, droped)
                player.sendMessage(EtherCore.minid("<dark_gray>[<red>喜报<dark_gray>] <white>你因为死亡丢失了 <green>${droped} <dark_green>E"))

                if (evt.damager is Player) {
                    val damager = evt.damager as Player
                    damager.sendMessage(EtherCore.minid("<dark_gray>[<green>经济<dark_gray>] <white>你因为击杀 <yellow>${player.name} <white>获得了 <green>${droped} <dark_green>E"))
                    Balance.add(damager, droped)
                }
            }
        }
    }

}
