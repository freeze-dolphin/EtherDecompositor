package io.sn.etherdec.modules

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack
import io.github.thebusybiscuit.slimefun4.implementation.items.magical.SoulboundItem
import io.sn.etherdec.EtherCore
import io.sn.etherdec.objects.AbstractModule
import org.bukkit.Material

class SoulboundItemsList(plug: EtherCore) : AbstractModule(plug) {

    override fun postSetup() {
        SlimefunItem(
            plug.group,
            SlimefunItemStack("ETHERITE_TRACKER", Material.COMPASS, "&e追踪器&r", "", "&e右键 &f追踪距离最近的玩家"),
            type,
            nullRecipe
        ).register(
            plug
        )

        SoulboundItem(
            plug.group,
            SlimefunItemStack(
                "ETHERITE_TRACKER_SOULBOUND",
                Material.COMPASS,
                "&e追踪器 &8(&c灵魂绑定&8)&r",
                "",
                "&e右键 &f追踪距离最近的玩家"
            ),
            type,
            nullRecipe
        ).register(
            plug
        )

        SoulboundItem(
            plug.group,
            SlimefunItemStack("ETHERITE_RECOVERY_COMPASS_SOULBOUND", Material.RECOVERY_COMPASS, "&b追溯指针 &8(&c灵魂绑定&8)&r"),
            type,
            nullRecipe
        ).register(
            plug
        )
    }

}
