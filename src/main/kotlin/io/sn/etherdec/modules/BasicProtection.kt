package io.sn.etherdec.modules

import io.papermc.paper.event.player.PlayerItemFrameChangeEvent
import io.sn.etherdec.EtherCore
import io.sn.etherdec.objects.AListener
import io.sn.etherdec.objects.AbstractModule
import org.bukkit.event.EventHandler
import org.bukkit.event.hanging.HangingBreakByEntityEvent

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


}
