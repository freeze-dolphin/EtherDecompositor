package io.sn.etherdec.objects

import io.sn.etherdec.EtherCore
import org.bukkit.event.Listener

abstract class AbstractModule(val plug: EtherCore) : Listener {

    fun setup() {
        preSetup()

        // thing to do on default setup
        plug.server.pluginManager.registerEvents(this, plug)

        postSetup()
    }


    @Suppress("unused")
    open fun postSetup() {
    }

    @Suppress("unused")
    open fun preSetup() {
    }

}