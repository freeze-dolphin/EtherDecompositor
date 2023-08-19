package io.sn.etherdec.modules

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack
import io.sn.etherdec.EtherCore
import io.sn.etherdec.objects.AbstractModule
import io.sn.etherdec.objects.slimefun.SupplyChest
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class ChestAutoStuff(plug: EtherCore) : AbstractModule(plug) {

    override fun postSetup() {
        SupplyChest(
            plug,
            SlimefunItemStack("ETHERITE_SUPPLY_CHEST", ItemStack(Material.CHEST), "&e补给箱&r"),
            type,
            nullRecipe
        ).register(plug)
    }


}
