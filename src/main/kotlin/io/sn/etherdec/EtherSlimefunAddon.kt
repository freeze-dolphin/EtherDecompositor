package io.sn.etherdec

import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon
import java.util.logging.Logger

interface EtherSlimefunAddon : SlimefunAddon {

    override fun getName(): String

    override fun getLogger(): Logger

}
