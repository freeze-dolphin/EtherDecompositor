package io.sn.etherdec.modules

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler
import io.sn.etherdec.EtherCore
import io.sn.etherdec.objects.AbstractModule
import me.deecaad.weaponmechanics.WeaponMechanics
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import org.bukkit.persistence.PersistentDataType

class Ammo(plug: EtherCore) : AbstractModule(plug) {

    override fun postSetup() {
        AmmoList.ammoNames.zip(AmmoList.ammoIds).forEach {
            registerAmmo(it.first, it.second)
        }

        SlimefunItem(
            plug.group,
            SlimefunItemStack(
                "ETHERITE_GENERIC_AMMO_BOX",
                Material.GOLDEN_HOE,
                "&b通用武器弹药箱&r",
                "",
                "&e副手右键 &f为主手的武器填充一次弹药"
            ).apply {
                editMeta {
                    ItemFlag.values().forEach { flg ->
                        it.addItemFlags(flg)
                    }
                }
            },
            type,
            nullRecipe
        ).apply {
            addItemHandler(ItemUseHandler {
                if (it.hand == EquipmentSlot.OFF_HAND) {
                    val main = it.player.equipment.itemInMainHand

                    val wpTitle = WeaponMechanics.getWeaponHandler().infoHandler.getWeaponTitle(main, true) ?: return@ItemUseHandler

                    if (WeaponMechanics.getWeaponHandler().reloadHandler.getAmmoLeft(main, wpTitle) > 0) return@ItemUseHandler

                    val wpCfg = WeaponMechanics.getConfigurations()
                    val rldNum = wpCfg.getInt("$wpTitle.Reload.Magazine_Size", -1)

                    if (rldNum < 1) return@ItemUseHandler


                    it.player.world.playSound(it.player, Sound.ENTITY_HORSE_SADDLE, 1f, 0.6f)
                    it.player.world.playSound(it.player, Sound.ENTITY_HORSE_ARMOR, 1f, 0.6f)
                    main.editMeta { im ->
                        im.persistentDataContainer[NamespacedKey.fromString("weaponmechanics:ammo-left")!!, PersistentDataType.INTEGER] =
                            rldNum
                    }

                    it.item.editMeta { im ->
                        val dmgb = im as Damageable
                        if (!dmgb.isUnbreakable) {
                            val cost = plug.config.getInt("ammo.generic-box-cost-per-use", 8)

                            dmgb.damage += cost
                            if (dmgb.damage >= it.item.type.maxDurability) {
                                it.item.amount -= 1
                                it.player.world.playSound(it.player, Sound.ENTITY_ITEM_BREAK, 1f, 1f)
                                it.player.world.spawnParticle(Particle.ITEM_CRACK, it.player.location, 1, it.item)
                                return@editMeta
                            }
                        }
                    }
                }
            })
        }.register(plug)
    }

    private fun registerAmmo(name: String, id: String) {
        SlimefunItem(
            plug.group, SlimefunItemStack(
                "ETHERITE_AMMO_${id.uppercase()}", ItemStack(Material.PRISMARINE_SHARD).apply {
                    editMeta {
                        it.persistentDataContainer[NamespacedKey.fromString("weaponmechanics:ammo-name")!!, PersistentDataType.STRING] =
                            id
                        if (name.contains('夹') || name.contains('盒')) {
                            it.persistentDataContainer[NamespacedKey.fromString("weaponmechanics:ammo-magazine")!!, PersistentDataType.INTEGER] =
                                1
                        }
                    }
                }, "&f弹药 &7- &e$name"
            ), type, nullRecipe
        ).register(plug)
    }

}

object AmmoList {

    val ammoIds = arrayOf(
        "12GINCENDIARYAMMO",
        "SIDEWINDER7AMMO",
        "HESNIPERAMMO",
        "FAMASAMMO",
        "PR3OSCAMMO",
        "PR3AMMO",
        "SPASAMMO",
        "AN94AMMO",
        "MP5AMMO",
        "Z20SIGNETAMMO",
        "M14AMMO",
        "BARRETTEXPLOSIVEAMMO",
        "ROCKETPOD57MMAMMO",
        "BARRETTAMMO",
        "M1887AMMO",
        "M40A3AMMO",
        "12GBUCKSHOTAMMO",
        "SKORPIONAMMO",
        "BIZONAMMO",
        "SIGP226AMMO",
        "FLAREGUNBLUEAMMO",
        "SPLITTERCANNONREFRACTEDAMMO",
        "B4CROSSBOWAMMO",
        "P90AMMO",
        "KCASMARTCARBINEAMMO",
        "SG550AMMO",
        "NTW20AMMO",
        "EMP4AMMO",
        "AMA40MMSPOTLIGHTAMMO",
        "STINGERAMMO",
        "MINIUZIAMMO",
        "M72LAWAMMO",
        "HIGHEXPISTOLAMMO",
        "GAU19AMMO",
        "BARRETTCOMPACTAMMO",
        "R700AMMO",
        "Z10LIGHTRIFLEAMMO",
        "AK47AMMO",
        "TRIPLETAKEAMMO",
        "SEEKERRIFLEAMMO",
        "FNSCARAMMO",
        "FLAREGUNBLACKAMMO",
        "FLAREGUNGREENAMMO",
        "MINIGUNEXPLOSIVEAMMO",
        "WITHERINGAKAMMO",
        "12GBIRDSHOTAMMO",
        "L86AMMO",
        "G3AMMO",
        "ACRAMMO",
        "AUGAMMO",
        "FLAMETHROWERAMMO",
        "GAU24MMHEAMMO",
        "HYDRA70AMMO",
        "MTARAMMO",
        "STARSAMMO",
        "FLAREGUNWHITEAMMO",
        "ASH12AMMO",
        "M1014AMMO",
        "AA12AMMO",
        "12GSLUGAMMO",
        "MINIGUNAMMO",
        "GAU20MMARCAMMO",
        "KRISSVECTORAMMO",
        "R870AMMO",
        "ADAR15AMMO",
        "GAU30MMAMMO",
        "FLAREGUNAMMO",
        "STARSNCI3AMMO",
        "DESERTEAGLEAMMO",
        "MLRS6AMMO",
        "M60E4AMMO",
        "DRAGUNOVAMMO",
        "SHULKERSNIPERAMMO",
        "MWAMMOBAG",
        "AMA40MMAMMO",
        "AT4AMMO",
        "GAU30MMHEAMMO",
        "DEVOTIONX55AMMO",
        "KCASLUGGERAMMO",
        "12GHIGHEXPLOSIVEAMMO",
        "W1200AMMO",
        "M16A4AMMO",
        "M249AMMO",
        "GLOCKAMMO",
        "HONEYBADGERAMMO",
        "RPGAMMO",
        "12GFLECHETTEAMMO",
        "L96AMMO",
        "PREDATOR37MMSRAMMO",
        "GRENADELAUNCHERAMMO",
        "M21AMMO",
        "PAW20AMMO",
        "12GPOISONEDFLECHETTEAMMO",
        "GRENADELAUNCHERINCENDIARYAMMO",
        "GRENADELAUNCHERBOUNCYAMMO",
        "GALILAMMO",
        "RPKAMMO",
        "AU40MMHEAMMO",
        "HCARAMMO",
        "KCASMART50AMMO",
        "APCRRIFLEAMMO",
        "GAU20MMAMMO",
        "W1200INCENDIARYAMMO",
        "USPPOISONAMMO",
        "ROCKETPOD68MMAMMO",
        "SNIPERAMMO",
        "PANZERFAUST3AMMO",
        "MINIGUNAMMOPORTABLE",
        "BASICSALVOAMMO",
        "COLDWARE2AMMO",
        "GRENADELAUNCHERGASAMMO",
        "AU40MMAMMO",
        "RPDAMMO",
        "G36AMMO",
        "Z45SENTINELAMMO",
        "AK74AMMO",
        "ADVANCEDSALVOAMMO",
        "KCASMARTPISTOLAMMO",
        "A91AMMO",
        "SPLITTERCANNONAMMO",
        "PREDATOR37MMAMMO",
        "PREDATOR37MMLRAMMO",
        "SMG100AMMO",
        "GRENADELAUNCHERIMPACTAMMO",
        "VOLTV3AMMO",
        "JURYAMMO",
        "STARSBURNINGRUBBERAMMO",
        "GAU20MMHVLEAMMO",
        "GAU24MMAMMO",
        "KCASLUGGERKINETICAMMO",
        "USPAMMO",
        "COLT45AMMO",
        "M9AMMO",
        "Z16SPLICERRIFLEAMMO",
        "MASTIFF1218AMMO",
        "APPISTOLAMMO",
        "Z15SPLICERAMMO"
    )

    val ammoNames = arrayOf(
        "12G 燃烧弹",
        "SideWinder7 弹夹",
        "手枪子弹 &8(&f9.06mm&8)", // .357 Magnum [HESNIPERAMMO]
        "突击步枪弹夹 &8(&f5.56mm&8)", // FR_5.56, M4A1, AUG [FAMASAMMO]
        "PR3OSC",
        "PR3",
        "SPAS",
        "AN94",
        "MP5",
        "Z20SIGNET",
        "M14",
        "BARRETTEXPLOSIVE",
        "ROCKETPOD57MM",
        "BARRETT",
        "M1887",
        "M40A3",
        "12GBUCKSHOT",
        "SKORPION",
        "BIZON",
        "SIGP226",
        "FLAREGUNBLUE",
        "SPLITTERCANNONREFRACTED",
        "B4CROSSBOW",
        "P90",
        "KCASMARTCARBINE",
        "SG550",
        "NTW20",
        "EMP4",
        "AMA40MMSPOTLIGHT",
        "STINGER",
        "Uzi 冲锋枪弹夹", // Uzi [MINIUZIAMMO]
        "M72LAW",
        "狙击枪子弹 &8(&f12.7mm&8)", // AX_50 [HIGHEXPISTOLAMMO]
        "GAU19",
        "BARRETTCOMPACT",
        "R700",
        "Z10LIGHTRIFLE",
        "突击步枪弹夹 &8(&f7.62mm&8)", // AK_47, FN_FAL [AK47AMMO]
        "TRIPLETAKE",
        "SEEKERRIFLE",
        "FNSCARAMMO",
        "FLAREGUNBLACK",
        "FLAREGUNGREEN",
        "MINIGUNEXPLOSIVE",
        "WITHERINGAK",
        "12GBIRDSHOT",
        "L86",
        "突击步枪弹夹 &8(&f7.92mm&8)", // STG44 [G3AMMO]
        "ACR",
        "AUG",
        "FLAMETHROWER",
        "GAU24MMHE",
        "HYDRA70",
        "MTAR",
        "STARS",
        "FLAREGUNWHITE",
        "ASH12",
        "M1014",
        "AA12",
        "12GSLUG",
        "MINIGUN",
        "GAU20MMARC",
        "KRISSVECTORAMMO",
        "R870",
        "ADAR15",
        "GAU30MM",
        "FLAREGUN",
        "STARSNCI3",
        "手枪弹夹 &8(&f12.7mm&8)", // 50_GS (DesertEagle) [DESERTEAGLEAMMO]
        "MLRS6",
        "M60E4",
        "DRAGUNOV",
        "SHULKERSNIPER",
        "MW ammoBAG",
        "AMA40MM",
        "小型核弹弹头", // Fatman [AT4AMMO]
        "GAU30MMHE",
        "DEVOTIONX55",
        "KCASLUGGER",
        "12GHIGHEXPLOSIVE",
        "霰弹枪子弹 &8(&f12 Gauge&8)", // Origin_12, R9_0 [W1200AMMO]
        "M4A1 弹夹",
        "M249",
        "格洛克弹夹",
        "HONEYBADGER",
        "RPG-7 火箭", // RPG_7 [RPGAMMO]
        "12GFLECHETTE",
        "L96",
        "PREDATOR37MMSR",
        "榴弹 &8(&f40mm&8)",
        "M21",
        "PAW20",
        "12GPOISONEDFLECHETTE",
        "榴弹 &8(&c燃烧&8)",
        "榴弹 &8(&a弹跳&8)",
        "GALIL",
        "RPK",
        "AU40MMHE",
        "HCAR",
        "KCASMART50",
        "APCRRIFLE",
        "GAU20MM",
        "W1200INCENDIARY",
        "USPPOISON",
        "ROCKETPOD68MM",
        "毛瑟步枪子弹 &8(&f7.92mm&8)", // [SNIPERAMMO] for Kar98k, can be used to craft 毛瑟步枪弹药盒 &8(&f7.92mm&8)
        "PANZERFAUST3",
        "MINIGUN ammoPORTABLE",
        "BASICSALVO",
        "COLDWARE2",
        "榴弹 &8(&d毒气&8)",
        "AU40MM",
        "RPD",
        "G36",
        "Z45SENTINEL",
        "AK74",
        "ADVANCEDSALVO",
        "KCASMARTPISTOL",
        "A91",
        "SPLITTERCANNON",
        "PREDATOR37MM",
        "PREDATOR37MMLR",
        "SMG100",
        "榴弹 &8(&f闪光&8)",
        "VOLTV3",
        "JURY",
        "STARSBURNINGRUBBER",
        "GAU20MMHVLE",
        "毛瑟步枪弹药盒 &8(&f7.92mm&8)", // MG34 [GAU24MMAMMO]
        "KCASLUGGERKINETIC",
        "USP",
        "COLT45",
        "M9",
        "Z16SPLICERRIFLE",
        "MASTIFF1218",
        "APPISTOL",
        "Z15SPLICER"
    )

}