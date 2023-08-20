package io.sn.etherdec.modules

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack
import io.github.thebusybiscuit.slimefun4.implementation.items.magical.SoulboundItem
import io.sn.etherdec.EtherCore
import io.sn.etherdec.objects.AbstractModule
import org.bukkit.Material

class Pager(plug: EtherCore) : AbstractModule(plug) {

    override fun postSetup() {
        SoulboundItem(
            plug.group,
            SlimefunItemStack("ETHERITE_PAGER", Material.CLOCK, "&e传呼机&r", "", "&e右键 &f打开传呼机界面"),
            type,
            nullRecipe
        ).register(plug)
    }

}
