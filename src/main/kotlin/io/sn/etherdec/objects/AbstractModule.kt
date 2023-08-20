package io.sn.etherdec.objects

import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType
import io.sn.etherdec.EtherCore
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack

abstract class AbstractModule(val plug: EtherCore) : Listener {

    protected val nullRecipe = arrayOfNulls<ItemStack>(9)
    protected val type: RecipeType = RecipeType.NULL

    fun setup() {
        preSetup()

        // thing to do on default setup
        if (this is AListener) {
            plug.logger.info("Registering listener for module: ${this.javaClass.name}")
            plug.server.pluginManager.registerEvents(this, plug)
        }

        postSetup()
    }

    @Suppress("unused")
    open fun postSetup() {
    }

    @Suppress("unused")
    open fun preSetup() {
    }

}