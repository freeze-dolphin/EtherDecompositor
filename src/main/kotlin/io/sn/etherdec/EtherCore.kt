package io.sn.etherdec

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack
import io.sn.etherdec.modules.*
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class EtherCore : JavaPlugin(), EtherSlimefunAddon {

    lateinit var dumpedItems: YamlConfiguration

    companion object {
        private const val DEFAULT_PREFIX = "<dark_gray>[<color:#FFDAB9>系统<dark_gray>] "
        private val mini: MiniMessage = MiniMessage.miniMessage()
        private val plain: PlainTextComponentSerializer = PlainTextComponentSerializer.plainText()

        fun minid(s: String): Component = mini.deserialize(s)
        fun plains(s: Component): String = plain.serialize(s)
    }

    val group = ItemGroup(
        NamespacedKey(this, "etherite_dec"), CustomItemStack(
            Material.CHEST,
            "&dEtherite Decompositor&f"
        ), 4
    )

    fun sendMsg(plr: Player, s: String) {
        plr.sendMessage(minid(config.getString("prefix", DEFAULT_PREFIX)!! + s))
    }

    fun sendMsg(plr: Player, s: Component) {
        plr.sendMessage(minid(config.getString("prefix", DEFAULT_PREFIX)!!).append(s))
    }

    override fun onEnable() {
        logger.info("`Ether Decompisitor` is ready in sit. ;)")

        setupConfig()
        setupModules()
    }

    private fun setupModules() {
        Command(this).setup()
        MonsterOptimize(this).setup()
        Bleeding(this).setup()
        ChestAutoStuff(this).setup()
        KillAndDrop(this).setup()
    }

    private fun setupConfig() {
        saveDefaultConfig()
        val dpFile = File(dataFolder.path + File.separator + "dumped.yml")
        dumpedItems = YamlConfiguration.loadConfiguration(dpFile)
    }

    override fun getJavaPlugin(): JavaPlugin = this

    override fun getBugTrackerURL(): String = "https://github.com/freeze-dolphin/EtherDecompositor/issues"

}
