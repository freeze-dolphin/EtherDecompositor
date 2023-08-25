package io.sn.etherdec.modules

import com.spawnchunk.emeraldbank.modules.Balance
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.CommandPermission
import dev.jorel.commandapi.arguments.GreedyStringArgument
import dev.jorel.commandapi.arguments.IntegerArgument
import dev.jorel.commandapi.arguments.PlayerArgument
import dev.jorel.commandapi.arguments.StringArgument
import dev.jorel.commandapi.executors.CommandExecutor
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import io.sn.etherdec.EtherCore
import io.sn.etherdec.objects.AbstractModule
import nl.vv32.rcon.Rcon
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player
import java.io.IOException


class Command(plug: EtherCore) : AbstractModule(plug) {

    override fun postSetup() {
        CommandAPICommand("sudo").withPermission(CommandPermission.OP)
            .withArguments(PlayerArgument("player"), GreedyStringArgument("command")).executes(CommandExecutor { _, args ->
                val player = args[0] as Player
                val command = args[1] as String

                if (command.startsWith("c:")) {
                    player.chat(command.split("c:")[1])
                } else {
                    player.performCommand(command)
                }
            }).register()

        CommandAPICommand("invsee").withAliases("inv").withPermission(CommandPermission.OP).withArguments(PlayerArgument("player"))
            .executesPlayer(PlayerCommandExecutor { sender, args ->
                val player = args[0] as Player

                sender.openInventory(player.inventory)
            }).register()

        CommandAPICommand("enderchest").withAliases("ec").withPermission(CommandPermission.OP).withArguments(PlayerArgument("player"))
            .executesPlayer(PlayerCommandExecutor { sender, args ->
                val player = args[0] as Player

                sender.openInventory(player.enderChest)
            }).register()
        CommandAPICommand("logging").withAliases("log").withPermission(CommandPermission.OP).withArguments(GreedyStringArgument("info"))
            .executes(CommandExecutor { sender, args ->
                val info = args[0] as String

                if (sender !is ConsoleCommandSender) {
                    sender.sendMessage(EtherCore.minid("<dark_gray>[<yellow>日志<dark_gray>] <white>信息已记录到控制台: <yellow>$info"))
                }
                Bukkit.getConsoleSender().sendMessage(info)
            }).register()

        CommandAPICommand("ether").withPermission(CommandPermission.OP).withAliases("eth", "et").withSubcommand(
            CommandAPICommand("dump").withArguments(StringArgument("id")).withPermission(CommandPermission.OP)
                .executesPlayer(PlayerCommandExecutor { sender, args ->
                    val id = args["id"] as String
                    if (plug.dumpedItems.contains(id)) {
                        plug.sendMsg(
                            sender, EtherCore.minid("<red>该 id 已存在: ").append(plug.dumpedItems.getItemStack(id)!!.displayName())
                        )
                    } else {
                        val hand = sender.inventory.itemInMainHand
                        if (hand.type == Material.AIR) {
                            plug.sendMsg(sender, "<red>你必须拿着一个物品才能执行记录")
                        } else {
                            plug.dumpedItems.set(id, hand)
                            plug.dumpedItems.save(plug.dpFile)
                        }
                    }
                })
        ).withSubcommand(
            CommandAPICommand("withdraw").withPermission(CommandPermission.OP)
                .withArguments(PlayerArgument("player"), StringArgument("amount")).executes(CommandExecutor { _, args ->
                    val player = args[0] as Player
                    val amount = args[1] as String
                    val am = if (amount == "all") Balance.bankBalance(player) else amount.toInt()
                    Balance.payout(player, player, am as Double?)
                })
        ).withSubcommand(
            CommandAPICommand("deposit").withPermission(CommandPermission.OP)
                .withArguments(PlayerArgument("player"), StringArgument("type")).executes(CommandExecutor { _, args ->
                    val player = args[0] as Player
                    val amount = args[1] as String
                    if (amount == "all") Balance.convertAll(player, player) else Balance.convert(player, player)
                })
        ).withSubcommand(
            CommandAPICommand("convert").withPermission(CommandPermission.OP).withArguments(PlayerArgument("player"))
                .executes(CommandExecutor { _, args ->
                    val player = args[0] as Player

                    try {
                        Rcon.open(plug.config.getString("rcon.host"), plug.config.getInt("rcon.port")).use { rcon ->
                            if (rcon.authenticate(plug.config.getString("rcon.passwd"))) {
                                val actual = 1152 / plug.config.getDouble("exchange-rate", 10.0)
                                val echo = rcon.sendCommand("offlinepay ${player.name} $actual")
                                if (echo.isNotEmpty()) plug.logger.info(echo)
                                Balance.remove(player, 1152.0)
                                plug.logger.info("Transaction completed: ${player.name} yetzirah 1152 -> assiash $actual")
                            } else {
                                plug.logger.severe("Failed to authenticate")
                            }
                        }
                    } catch (ioe: IOException) {
                        plug.logger.warning("Failed due to IOException, maybe not connected to Internet")
                    }
                })
        ).withSubcommand(
            CommandAPICommand("bankadd").withPermission(CommandPermission.OP)
                .withArguments(PlayerArgument("player"), IntegerArgument("number")).executes(CommandExecutor { _, args ->
                    val plr = args[0] as Player
                    val amount = args[1] as Int

                    Balance.add(plr, amount.toDouble())
                })
        ).withSubcommand(
            CommandAPICommand("reload").withPermission(CommandPermission.OP).executes(CommandExecutor { _, _ ->
                plug.reloadConfig()
            })
        ).register()
    }

}
