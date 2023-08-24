@file:Suppress("DEPRECATION")

package io.sn.etherdec.modules

import com.spawnchunk.emeraldbank.modules.Balance
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils
import io.sn.etherdec.EtherCore
import io.sn.etherdec.objects.AbstractModule
import io.sn.slimefun4.ChestMenuTexture
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.interfaces.InventoryBlock
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta

class ATM(plug: EtherCore) : AbstractModule(plug) {

    override fun postSetup() {
        ATMBlock(
            plug, SlimefunItemStack(
                "ETHERITE_ATM_BLOCK",
                SlimefunUtils.getCustomHead("5f7d517c12ee1dcd90b6fe85fd6c70ab15ed8840bba5c0b5815a0d00674b68a9"),
                "&eATM&r"
            ), type, nullRecipe
        ).register(plug)
    }

    class ATMBlock(plug: EtherCore, item: SlimefunItemStack, type: RecipeType, recipe: Array<ItemStack?>) :
        SlimefunItem(plug.group, item, type, recipe), InventoryBlock {

        private val border = intArrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 13, 17, 18, 26, 27, 31, 35, 36, 37, 39, 40, 41, 43, 44)

        private val withdrawSlot = 42
        private val infoSlot = 38
        private val warnSlot = 22

        private val borderItem = CustomItemStack(Material.GRAY_STAINED_GLASS_PANE, " ").setCustomModel(4000)

        override fun getInputSlots(): IntArray = intArrayOf(10, 11, 12, 19, 20, 21, 28, 29, 30)

        override fun getOutputSlots(): IntArray = intArrayOf(14, 15, 16, 23, 24, 25, 32, 33, 34)

        init {
            (object : BlockMenuPreset(item.itemId, "&2ATM", ChestMenuTexture("dumortierite", "atm")) {
                override fun init() {
                    constructMenu(this)
                }

                override fun newInstance(menu: BlockMenu, b: Block) {
                    updateBlockInventory(menu)
                }

                override fun canOpen(b: Block, p: Player): Boolean {
                    return p.hasPermission("slimefun.inventory.bypass") || Slimefun.getProtectionManager()
                        .hasPermission(p, b.location, Interaction.INTERACT_BLOCK)
                }

                override fun getSlotsAccessedByItemTransport(flow: ItemTransportFlow?): IntArray {
                    return if (flow == ItemTransportFlow.INSERT) {
                        getInputSlots()
                    } else {
                        getOutputSlots()
                    }
                }

            })
        }

        private fun updateBlockInventory(menu: BlockMenu) {
            menu.replaceExistingItem(
                withdrawSlot,
                CustomItemStack(Material.BUCKET, "&e提款", "", "&e点击 &f从银行提款 &a64 &2E", "&e右键点击 &f从银行提款 &a576 &2E")
            )
            menu.addMenuClickHandler(withdrawSlot) { p, _, _, ac ->
                if (ac.isRightClicked) {
                    if (Balance.balance(p) >= 576) {
                        val out = ItemStack(Material.EMERALD_BLOCK, 64)
                        if (menu.fits(out, *outputSlots)) {
                            menu.pushItem(out, *outputSlots)
                            Balance.remove(p, 576.0)
                        }
                    }
                } else {
                    if (Balance.balance(p) >= 64) {
                        val out = ItemStack(Material.EMERALD, 64)
                        if (menu.fits(out, *outputSlots)) {
                            menu.pushItem(out, *outputSlots)
                            Balance.remove(p, 64.0)
                        }
                    }
                }
                updateBlockInventory(menu)
                false
            }
        }

        private fun constructMenu(preset: BlockMenuPreset) {
            border.forEach {
                preset.addItem(it, borderItem) { _, _, _, _ -> false }
            }
            inputSlots.forEach {
                preset.addMenuClickHandler(it) { _, _, _, _ ->
                    true
                }
            }
            outputSlots.forEach {
                preset.addMenuClickHandler(it) { _, _, _, _ ->
                    true
                }
            }
            preset.addItem(withdrawSlot, borderItem) { _, _, _, _ -> false }
            preset.addItem(infoSlot, borderItem) { _, _, _, _ -> false }
            preset.addItem(warnSlot, borderItem) { _, _, _, _ -> false }
        }

        override fun preRegister() {
            addItemHandler(object : BlockTicker() {
                override fun tick(b: Block, sf: SlimefunItem, data: SlimefunBlockData) {
                    this@ATMBlock.tick(b)
                }

                override fun isSynchronized(): Boolean {
                    return true
                }
            })
        }

        fun tick(b: Block) {
            val inv = StorageCacheUtils.getMenu(b.location) ?: return

            if (inv.toInventory().viewers.size > 1) {
                (inputSlots + outputSlots).forEach {
                    inv.addMenuClickHandler(it) { _, _, _, _ -> false }
                    inv.replaceExistingItem(
                        warnSlot,
                        CustomItemStack(Material.BARRIER, "&c警告&r", "", "&f当前打开容器的人不止你自己", "&f已经禁止此 ATM 内的物品移动")
                    )
                }
            } else {
                (inputSlots + outputSlots).forEach {
                    inv.addMenuClickHandler(it) { _, _, _, _ -> true }
                    inv.replaceExistingItem(warnSlot, borderItem)
                }
            }

            val near = b.location.getNearbyPlayers(1.0)
            if (near.size != 1) {
                inv.replaceExistingItem(infoSlot, ItemStack(Material.PLAYER_HEAD).apply {
                    editMeta {
                        it.displayName(EtherCore.minid("<!italic><yellow>操作人: <red>无"))
                        it.lore(
                            mutableListOf(
                                EtherCore.minid(""),
                                EtherCore.minid("<!italic><red>请确保当前操作人显示为你自己的名字"),
                                EtherCore.minid("<!italic><red>并且周围只有你一个人之后再操作"),
                            )
                        )
                    }
                })
                return
            }

            val target = near.elementAt(0)

            inv.replaceExistingItem(infoSlot, ItemStack(Material.PLAYER_HEAD).apply {
                editMeta {
                    it.displayName(EtherCore.minid("<!italic><yellow>操作人: <white>${target.name}"))
                    it.lore(
                        mutableListOf(
                            EtherCore.minid(""),
                            EtherCore.minid("<!italic><white>当前存款: <yellow>${Balance.balance(target)}"),
                            EtherCore.minid(""),
                            EtherCore.minid("<!italic><red>请确保当前操作人显示为你自己的名字"),
                            EtherCore.minid("<!italic><red>并且周围只有你一个人之后再操作"),
                        )
                    )
                    (it as SkullMeta).setOwningPlayer(target)
                }
            })

            inputSlots.forEach {
                val itm = inv.getItemInSlot(it)
                if (itm != null) {
                    if (itm.type == Material.EMERALD) {
                        val am = itm.amount
                        inv.consumeItem(it, am)
                        Balance.add(target, am.toDouble())
                    } else if (itm.type == Material.EMERALD_BLOCK) {
                        val am = itm.amount
                        inv.consumeItem(it, am)
                        Balance.add(target, am.toDouble() * 9)
                    }
                }
            }
        }

    }

}
