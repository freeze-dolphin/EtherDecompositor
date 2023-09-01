package io.sn.etherdec

import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.ProtocolManager
import de.cubbossa.commonsettings.SettingsAPI
import fr.minuskube.netherboard.Netherboard
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack
import io.sn.etherdec.modules.*
import io.sn.etherdec.objects.AbstractModule
import io.sn.etherdec.objects.Unregisterable
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import kotlin.io.path.Path

@Suppress("unused")
class EtherCore : JavaPlugin(), EtherSlimefunAddon {

    lateinit var dumpedItems: YamlConfiguration

    val dpFile = File(dataFolder.path + File.separator + "dumped.yml")

    companion object {
        private const val DEFAULT_PREFIX = "<dark_gray>[<color:#FFDAB9>系统<dark_gray>] "
        private val mini: MiniMessage = MiniMessage.miniMessage()
        private val plain: PlainTextComponentSerializer = PlainTextComponentSerializer.plainText()
        private val json: JSONComponentSerializer = JSONComponentSerializer.json()
        private val legacy: LegacyComponentSerializer = LegacyComponentSerializer.legacy('&')

        lateinit var ptMan: ProtocolManager
        lateinit var settings: SettingsAPI
        lateinit var board: Netherboard

        fun minid(s: String): Component = mini.deserialize(s)
        fun plains(s: Component): String = plain.serialize(s)
        fun jsond(jsonStr: String): Component = json.deserialize(jsonStr)
        fun legacyd(s: String): Component = legacy.deserialize(s)
    }

    val group = ItemGroup(
        NamespacedKey(this, "etherite_dec"), CustomItemStack(
            Material.CHEST, "&dEtherite Decompositor&f"
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

        ptMan = ProtocolLibrary.getProtocolManager()
        settings = SettingsAPI.getInstance()
        board = Netherboard.instance()

        modules = arrayOf(
            Command(this),
            MonsterOptimize(this),
            Bleeding(this),
            ChestAutoStuff(this),
            KillAndDrop(this),
            BasicProtection(this),
            Pager(this),
            Death(this),
            ATM(this),
            BasicAntiCheat(this),
            // TrackerCompass(this),
            // Chernobyl(this),
            SoulboundItemsList(this),
            ProtocolModifier(this),
            Ammo(this),
            MiscItems(this),
            Combat(this),
            // Scoreboard(this),
            BackpackVault(this),
        )

        setupConfig()
        setupSettings()
        setupModules()
    }

    lateinit var modules: Array<AbstractModule>

    private fun setupSettings() {
        // CommonSettings.setup()
    }

    private fun setupModules() {
        modules.forEach {
            it.setup()
        }
    }

    override fun onDisable() {
        modules.forEach {
            if (it is Unregisterable) {
                it.onDisable()
            }
        }
    }

    private fun setupConfig() {
        saveDefaultConfig()
        if (!dpFile.exists()) dpFile.createNewFile()
        dumpedItems = YamlConfiguration.loadConfiguration(dpFile)

        Path(dataFolder.path, "storage").toFile().let {
            if (!it.exists()) {
                it.mkdirs()
            }
        }

        Path(dataFolder.path, "statistics.yml").toFile().let {
            if (!it.exists()) {
                it.createNewFile()
            }
        }
    }

    override fun getJavaPlugin(): JavaPlugin = this

    override fun getBugTrackerURL(): String = "https://github.com/freeze-dolphin/EtherDecompositor/issues"

}
