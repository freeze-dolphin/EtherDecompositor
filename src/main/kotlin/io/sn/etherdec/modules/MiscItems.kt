package io.sn.etherdec.modules

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack
import io.sn.etherdec.EtherCore
import io.sn.etherdec.objects.AbstractModule
import org.bukkit.Material

class MiscItems(plug: EtherCore) : AbstractModule(plug) {

    override fun postSetup() {
        SlimefunItem(
            plug.group, SlimefunItemStack(
                "ETHERITE_ETHER_DUST", Material.PRISMARINE_SHARD, "&X&F&F&F&F&F&E以太尘&r"
            ), type, nullRecipe
        ).register(plug)
    }

}
