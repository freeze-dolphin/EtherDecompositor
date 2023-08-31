@file:Suppress("DEPRECATION")

package io.sn.etherdec.modules

import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils
import io.sn.etherdec.EtherCore
import io.sn.etherdec.findLastNewIndex
import io.sn.etherdec.getPermVariable
import io.sn.etherdec.objects.AbstractModule
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu
import org.bukkit.Material
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import kotlin.io.path.Path

class BackpackVault(plug: EtherCore) : AbstractModule(plug) {

    companion object {
        private fun fetchVaultFor(plug: EtherCore, plr: Player): Array<ItemStack?> {
            with(Path(plug.dataFolder.path, "storage", plr.uniqueId.toString() + ".yml").toFile()) {
                if (!exists()) {
                    createNewFile()
                }

                val yml = YamlConfiguration.loadConfiguration(this)

                if (!yml.contains("0")) {
                    return arrayOf()
                }

                val lidx = findLastNewIndex(0, yml) - 1

                return (0..lidx).map {
                    yml.getItemStack(it.toString())
                }.toTypedArray()
            }
        }

        private fun saveVaultFor(plug: EtherCore, plr: Player, contents: Array<ItemStack?>) {
            with(Path(plug.dataFolder.path, "storage", plr.uniqueId.toString() + ".yml").toFile()) {
                if (!exists()) {
                    createNewFile()
                }

                val yml = YamlConfiguration.loadConfiguration(this)

                contents.filter {
                    !SlimefunUtils.isItemSimilar(it, barr, true)
                }.sortedBy {
                    it?.type?.name
                }.forEachIndexed { index, itemStack ->
                    yml.set(index.toString(), itemStack)
                }
                yml.save(this)
            }
        }

        private val barr = CustomItemStack(Material.BARRIER, "&c&l未解锁&r")

        fun openGuiFor(plug: EtherCore, plr: Player) {
            val inv = ChestMenu("&8安全箱", ChestMenuUtils.getBlankTexture())

            val cur = getPermVariable(plr, "etherite.vault.size.", 0)
            (cur + 1..8).forEach {
                inv.addItem(it, barr, ChestMenuUtils.getEmptyClickHandler())
            }

            val contents = fetchVaultFor(plug, plr)
            contents.forEachIndexed { index, itemStack ->
                inv.replaceExistingItem(index, itemStack!!)
                inv.addMenuClickHandler(index) { _, _, _, _ -> true }
            }

            inv.setEmptySlotsClickable(true)
            inv.setPlayerInventoryClickable(true)

            inv.addMenuCloseHandler {
                saveVaultFor(plug, it, inv.contents.filter {itm ->
                    !SlimefunUtils.isItemSimilar(itm, barr, true)
                }.toTypedArray())
            }

            inv.open(plr)
        }
    }

}
