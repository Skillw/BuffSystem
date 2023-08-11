package com.skillw.buffsystem.internal.command

import com.skillw.buffsystem.BuffSystem
import com.skillw.buffsystem.internal.command.sub.BuffAddCommand
import com.skillw.buffsystem.internal.command.sub.BuffClearCommand
import com.skillw.buffsystem.internal.command.sub.BuffInfoCommand
import com.skillw.buffsystem.internal.command.sub.BuffRemoveCommand
import com.skillw.pouvoir.util.soundClick
import com.skillw.pouvoir.util.soundFail
import com.skillw.pouvoir.util.soundSuccess
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.mainCommand
import taboolib.common.platform.command.subCommand
import taboolib.module.lang.sendLang
import taboolib.platform.util.sendLang

@Suppress("UNUSED_ANONYMOUS_PARAMETER")
@CommandHeader(name = "buff", permission = "buff.command")
object BuffCommand {
    internal fun ProxyCommandSender.soundSuccess() {
        (this.origin as? Player?)?.soundSuccess()
    }

    internal fun ProxyCommandSender.soundFail() {
        (this.origin as? Player?)?.soundFail()
    }

    internal fun ProxyCommandSender.soundClick() {
        (this.origin as? Player?)?.soundClick()
    }

    @CommandBody
    val main = mainCommand {
        incorrectSender { sender, _ ->
            sender.sendLang("command-only-player")
        }
        incorrectCommand { sender, context, index, state ->
            sender.soundFail()
            sender.sendLang("command-valid")
        }
        execute<ProxyCommandSender> { sender, context, argument ->
            sender.soundSuccess()
            sender.sendLang("command-info")
        }

    }

    @CommandBody(permission = "buff.command.reload")
    val reload = subCommand {
        execute<ProxyCommandSender> { sender, _, _ ->
            BuffSystem.reload()
            sender.soundSuccess()
            sender.sendLang("command-reload")
        }
    }

    @CommandBody(permission = "buff.command.json")
    val json = subCommand {
        execute<CommandSender> { sender, _, _ ->
            sender.sendLang("command-json")
            (sender as? Player)?.soundSuccess()
            sender.sendMessage(*BuffSystem.conditionManager.descriptions().toTypedArray())
        }
    }

    @CommandBody(permission = "buff.command.info")
    val info = BuffInfoCommand.info

    @CommandBody(permission = "buff.command.add")
    val add = BuffAddCommand.add

    @CommandBody(permission = "buff.command.addEntity")
    val addEntity = BuffAddCommand.addEntity

    @CommandBody(permission = "buff.command.remove")
    val remove = BuffRemoveCommand.remove

    @CommandBody(permission = "buff.command.removeEntity")
    val removeEntity = BuffRemoveCommand.removeEntity

    @CommandBody(permission = "buff.command.removeIf")
    val removeIf = BuffRemoveCommand.removeIf

    @CommandBody(permission = "buff.command.clear")
    val clear = BuffClearCommand.clear
}