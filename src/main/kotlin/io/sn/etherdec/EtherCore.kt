package io.sn.etherdec

import io.sn.etherdec.modules.BleedingModule
import io.sn.etherdec.modules.ChestAutoStuff
import io.sn.etherdec.modules.MonsterOptimizeModule
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class EtherCore : JavaPlugin() {

    companion object {
        private val mini: MiniMessage = MiniMessage.miniMessage()
        fun minid(s: String): Component = mini.deserialize(s)
    }

    fun sendMsg(plr: Player, s: String) {
        plr.sendMessage(minid(config.getString("prefix") + s))
    }

    override fun onEnable() {
        logger.info("`Ether Decompisitor` is ready in sit. ;)")

        setupConfig()
        setupModules()
    }

    private fun setupModules() {
        MonsterOptimizeModule(this).setup()
        BleedingModule(this).setup()
        ChestAutoStuff(this).setup()
    }

    private fun setupConfig() {
        saveDefaultConfig()
    }

}
