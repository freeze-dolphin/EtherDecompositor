package io.sn.etherdec.modules

import de.tr7zw.nbtapi.NBT
import io.sn.etherdec.EtherCore
import io.sn.etherdec.objects.AbstractModule
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.Chest
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import kotlin.random.Random

class ChestAutoStuff(plug: EtherCore) : AbstractModule(plug) {

    // TODO 注意，为了玩家体验，应该添加开箱时小概率瞬间填充，尤其是服务器重启之后

    @EventHandler
    fun onOpenChest(evt: PlayerInteractEvent) {
        if (evt.hasBlock()) {
            val blk = evt.clickedBlock
            if (blk?.type == Material.CHEST) {
                val chest = blk.state as Chest
                if (chest.inventory.isEmpty) {
                    evt.setUseInteractedBlock(Event.Result.DENY)
                    NBT.modify(chest) {
                        it.setBoolean("isFilling", true)
                    }
                    plug.sendMsg(evt.player, "<yellow>箱子正在装填中...")
                    Bukkit.getScheduler().runTaskLater(plug, Runnable {
                        fillChest(chest)
                    }, plug.config.getLong("fill-delay"))
                } else {
                    if (NBT.get(chest) {
                            if (!it.hasTag("isFilling")) return@get false
                            it.getBoolean("isFilling")
                        }) {
                        plug.sendMsg(evt.player, "<yellow>箱子正在装填中...")
                    }
                }
            }
        }
    }

    private fun fillChest(chest: Chest) {
        repeat(Random.nextInt(2, 8)) {
            val size = chest.inventory.size
            chest.inventory.apply {
                setItem(Random.nextInt(0, size), nextLoot())
            }
        }
    }

    private fun nextLoot(): ItemStack? {
        val rnd = Random.nextInt(0, 1000)
        return when (rnd) {
            in 0 until 500 -> {
                arrayOf(
                    ItemStack(Material.IRON_INGOT),
                    ItemStack(Material.IRON_INGOT, 2),
                    ItemStack(Material.IRON_INGOT, 3)
                )[Random.nextInt(3)]
            }

            else -> null
        }
    }

}
