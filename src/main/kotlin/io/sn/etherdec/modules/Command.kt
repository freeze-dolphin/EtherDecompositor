package io.sn.etherdec.modules

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.CommandPermission
import dev.jorel.commandapi.arguments.StringArgument
import dev.jorel.commandapi.executors.CommandExecutor
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import io.sn.etherdec.EtherCore
import io.sn.etherdec.objects.AbstractModule
import org.bukkit.Material

class Command(plug: EtherCore) : AbstractModule(plug) {

    override fun postSetup() {
        CommandAPICommand("ether").withPermission(CommandPermission.OP).withAliases("eth", "et")
            .withSubcommand(
                CommandAPICommand("dump")
                    .withArguments(StringArgument("id"))
                    .withPermission(CommandPermission.OP)
                    .executesPlayer(PlayerCommandExecutor { sender, args ->
                        val id = args["id"] as String
                        if (plug.dumpedItems.contains(id)) {
                            plug.sendMsg(
                                sender,
                                EtherCore.minid("<red>该 id 已存在: ").append(plug.dumpedItems.getItemStack(id)!!.displayName())
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
            )
            .withSubcommand(
                CommandAPICommand("reload")
                    .withPermission(CommandPermission.OP)
                    .executes(CommandExecutor { _, _ ->
                        plug.reloadConfig()
                    })
            )
            .register()
    }

}
