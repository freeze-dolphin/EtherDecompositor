@file:Suppress("DEPRECATION")

package io.sn.etherdec.modules

import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun.getDatabaseManager
import io.github.thebusybiscuit.slimefun4.implementation.items.blocks.WitherProofBlock
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils
import io.sn.etherdec.EtherCore
import io.sn.etherdec.findLastNewIndex
import io.sn.etherdec.objects.AListener
import io.sn.etherdec.objects.AbstractModule
import me.deecaad.weaponmechanics.WeaponMechanics
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.interfaces.InventoryBlock
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.block.Chest
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import kotlin.io.path.Path
import kotlin.random.Random

class ChestAutoStuff(plug: EtherCore) : AbstractModule(plug), AListener {

    override fun postSetup() {
        SupplyChest(
            plug, SlimefunItemStack("ETHERITE_SUPPLY_CHEST", ItemStack(Material.CHEST), "&e补给箱&r"), type, nullRecipe
        ).register(plug)
    }

    @EventHandler
    fun convert(evt: PlayerInteractEvent) {
        if (evt.hasBlock()) {
            val bl = evt.clickedBlock
            if (bl?.type == Material.CHEST) {
                if (bl.world.name in plug.config.getStringList("filling.auto-convert-enabled-worlds")) {
                    if (StorageCacheUtils.hasBlock(bl.location)) {
                        if (StorageCacheUtils.isBlock(bl.location, "ETHERITE_SUPPLY_CHEST")) {
                            writeLocation(bl.world, bl)
                        }
                    } else {
                        val ch = bl.state as Chest
                        if (ch.inventory.size == 27) {
                            getDatabaseManager().getBlockDataController(bl.world).createBlock(bl.location, "ETHERITE_SUPPLY_CHEST")
                            evt.isCancelled = true
                        }
                    }
                }
            }
        }
    }

    private fun writeLocation(world: World, bl: Block) {
        val f = Path(plug.dataFolder.path, "locations.yml").toFile()
        if (!f.exists()) {
            f.createNewFile()
        }

        YamlConfiguration.loadConfiguration(f).apply {
            if (!contains(world.name)) {
                set("${world.name}.0", bl.location)
            } else {
                val lastNewIdx = findLastNewIndex(0, getConfigurationSection(world.name)!!)
                if (!(0 until lastNewIdx).map {
                        getLocation("${world.name}.$it")
                    }.contains(
                        bl.location
                    )
                ) {
                    set("${world.name}.$lastNewIdx", bl.location)
                }
            }
        }.save(f)
    }

    class SupplyChest(
        private val plug: EtherCore, item: SlimefunItemStack, recipeType: RecipeType, recipe: Array<ItemStack?>
    ) : WitherProofBlock(plug.group, item, recipeType, recipe), InventoryBlock {

        private var internalTick: Long = 0

        init {
            createPreset(
                this, "&8补给箱", ChestMenuUtils.getBlankTexture()
            ) {
                constructMenu(it)
            }
        }

        private fun constructMenu(preset: BlockMenuPreset) {
            (0..35).forEach {
                preset.addItem(it, CustomItemStack(Material.BLACK_STAINED_GLASS_PANE, " ")) { _, _, _, _ -> false }
            }
            preset.addMenuOpeningHandler {
                it.world.playSound(it, Sound.BLOCK_CHEST_OPEN, 1f, 1f)
            }
        }

        override fun preRegister() {
            addItemHandler(object : BlockTicker() {
                override fun tick(b: Block, sf: SlimefunItem, data: SlimefunBlockData) {
                    this@SupplyChest.tick(b)
                }

                override fun isSynchronized(): Boolean {
                    return false
                }
            })
        }

        fun tick(b: Block) {
            val inv = StorageCacheUtils.getMenu(b.location) ?: return

            (0..35).forEach {
                if (inv.getItemInSlot(it) != null) {
                    if (inv.getItemInSlot(it).type == Material.BLACK_STAINED_GLASS_PANE) {
                        inv.replaceExistingItem(it, null)
                        inv.addMenuClickHandler(it) { _, _, _, _ -> true }
                    }
                }
            }

            inv.setEmptySlotsClickable(true)
            inv.setPlayerInventoryClickable(true)

            if (inv.toInventory().isEmpty) {
                if (internalTick > plug.config.getLong("filling.fill-delay")) {
                    internalTick = 0
                    fillChest(inv)
                    return
                }
                internalTick += 1
            }
        }

        private fun fillChest(inv: BlockMenu) {
            val (from, until) = plug.config.getString("filling.fill-try-times", "6,8")!!.split(",").map { it.toInt() }
            repeat(Random.nextInt(from, until)) {
                val size = inv.toInventory().size
                inv.apply {
                    replaceExistingItem(Random.nextInt(0, size), nextLoot())
                }
            }
        }

        private fun nextLoot(): ItemStack? {
            val lootTable = plug.config.getStringList("filling.loot-table")
            lootTable[Random.nextInt(lootTable.size)].let {
                val (chance, amount, itemId) = it.split(",")
                if (Random.nextDouble(1.0) < chance.toDouble()) {
                    val (type, id) = itemId.split(":")
                    return when (type.lowercase()) {
                        "wm" -> {
                            WeaponMechanics.getWeaponHandler().infoHandler.generateWeapon(id, 1)
                        }

                        "sf" -> {
                            getById(id)?.item
                        }

                        "va" -> {
                            Material.getMaterial(id)?.let { it1 -> ItemStack(it1) }
                        }

                        "dp" -> {
                            plug.dumpedItems.getItemStack(id, null)
                        }

                        else -> null
                    }?.let { fi ->
                        fi.clone().apply {
                            setAmount(amount.toInt())
                        }
                    }
                }
            }
            return null
        }

        override fun getInputSlots(): IntArray = intArrayOf(
            0,
            1,
            2,
            3,
            4,
            5,
            6,
            7,
            8,
            9,
            10,
            11,
            12,
            13,
            14,
            15,
            16,
            17,
            18,
            19,
            20,
            21,
            22,
            23,
            24,
            25,
            26,
            27,
            28,
            29,
            30,
            31,
            32,
            33,
            34,
            35
        )

        override fun getOutputSlots(): IntArray = intArrayOf()

    }


}
