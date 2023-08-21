package io.sn.etherdec.modules

import com.spawnchunk.emeraldbank.modules.Balance
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.CommandPermission
import dev.jorel.commandapi.arguments.PlayerArgument
import dev.jorel.commandapi.arguments.StringArgument
import dev.jorel.commandapi.arguments.TextArgument
import dev.jorel.commandapi.executors.CommandExecutor
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import io.sn.etherdec.EtherCore
import io.sn.etherdec.objects.AbstractModule
import org.bukkit.Material
import org.bukkit.entity.Player

class Command(plug: EtherCore) : AbstractModule(plug) {

    override fun postSetup() {
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
                        }
                    }
                })
        ).withSubcommand(
            CommandAPICommand("inv").withPermission(CommandPermission.OP).withArguments(PlayerArgument("player"))
                .executesPlayer(PlayerCommandExecutor { sender, args ->
                    val player = args[0] as Player

                    sender.openInventory(player.inventory)
                })
        ).withSubcommand(
            CommandAPICommand("ec").withPermission(CommandPermission.OP).withArguments(PlayerArgument("player"))
                .executesPlayer(PlayerCommandExecutor { sender, args ->
                    val player = args[0] as Player

                    sender.openInventory(player.enderChest)
                })
        ).withSubcommand(
            CommandAPICommand("sudo").withPermission(CommandPermission.OP).withArguments(PlayerArgument("player"), TextArgument("command"))
                .executes(CommandExecutor { _, args ->
                    val player = args[0] as Player
                    val command = args[1] as String

                    if (command.startsWith("c:")) {
                        player.chat(command.split("c:")[1])
                    } else {
                        player.performCommand(command)
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
            CommandAPICommand("reload").withPermission(CommandPermission.OP).executes(CommandExecutor { _, _ ->
                plug.reloadConfig()
            })
        ).register()
    }

}
