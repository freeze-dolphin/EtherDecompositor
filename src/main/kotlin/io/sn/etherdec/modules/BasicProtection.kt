package io.sn.etherdec.modules

import io.papermc.paper.event.player.PlayerItemFrameChangeEvent
import io.sn.etherdec.EtherCore
import io.sn.etherdec.objects.AListener
import io.sn.etherdec.objects.AbstractModule
import org.bukkit.Material
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.hanging.HangingBreakByEntityEvent
import org.bukkit.event.player.PlayerInteractEvent

class BasicProtection(plug: EtherCore) : AbstractModule(plug), AListener {

    @EventHandler
    fun protectPaintAndFrame(evt: HangingBreakByEntityEvent) {
        if (evt.remover == null) return
        if (!evt.remover!!.isOp) {
            evt.isCancelled = true
        }
    }

    @EventHandler
    fun interact(evt: PlayerItemFrameChangeEvent) {
        if (!evt.player.isOp) {
            evt.isCancelled = true
        }
    }

    @EventHandler
    fun noClickOnSomeObjects(evt: PlayerInteractEvent) {
        if (evt.player.isOp) return

        if (evt.hasBlock()) {
            val bl = evt.clickedBlock

            if (bl?.type in arrayOf(
                    Material.CAKE,
                    Material.NOTE_BLOCK,
                    Material.REPEATER,
                    Material.DRAGON_EGG,
                    Material.DAYLIGHT_DETECTOR,
                    Material.COMPARATOR
                )
            ) {
                evt.setUseInteractedBlock(Event.Result.DENY)
                return
            }
            if (bl?.type?.name?.startsWith("POTTED_") == true || bl?.type == Material.FLOWER_POT) {
                evt.setUseInteractedBlock(Event.Result.DENY)
                return
            }
        }
    }

}
