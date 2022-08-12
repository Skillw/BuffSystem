package com.skillw.buffsystem.internal.command

import com.skillw.buffsystem.BuffSystem
import com.skillw.buffsystem.BuffSystem.buffDataManager
import com.skillw.buffsystem.util.GsonUtils.parseToMap
import com.skillw.pouvoir.util.EntityUtils.getDisplayName
import com.skillw.pouvoir.util.EntityUtils.getEntityRayHit
import com.skillw.pouvoir.util.EntityUtils.livingEntity
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.ProxyPlayer
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.mainCommand
import taboolib.common.platform.command.subCommand
import taboolib.common.platform.function.onlinePlayers
import taboolib.common.platform.function.submit
import taboolib.module.chat.TellrawJson
import taboolib.module.chat.colored
import taboolib.module.lang.asLangText
import taboolib.module.lang.sendLang
import taboolib.platform.util.sendLang
import java.util.*

@Suppress("UNUSED_ANONYMOUS_PARAMETER")
@CommandHeader(name = "buff", permission = "buff.command")
object BuffCommand {

    @CommandBody
    val main = mainCommand {
        incorrectSender { sender, _ ->
            sender.sendLang("command-only-player")
        }
        incorrectCommand { sender, context, index, state ->
            sender.sendLang("command-valid")
        }
        execute<ProxyCommandSender> { sender, context, argument ->
            if (!sender.hasPermission("buff.command.help")) {
                sender.sendLang("command-no-permission")
                return@execute
            }
            sender.sendLang("command-info")
        }

    }

    @CommandBody
    val add = subCommand {
        dynamic {
            suggestion<ProxyCommandSender> { sender, context ->
                onlinePlayers().map { it.name }
            }
            dynamic {
                dynamic {
                    suggestion<ProxyCommandSender> { sender, context ->
                        BuffSystem.buffManager.map { it.key }
                    }
                    dynamic {
                        suggestion<ProxyCommandSender>(uncheck = true) { sender, context ->
                            listOf("{}")
                        }
                        execute<ProxyCommandSender> { sender, context, argument ->
                            if (!sender.hasPermission("buff.command.add")) {
                                sender.sendLang("command-no-permission")
                                return@execute
                            }
                            val player = Bukkit.getPlayer(context.argument(-3))
                            if (player == null) {
                                sender.sendLang("command-valid-player", context.argument(-3))
                                return@execute
                            }
                            giveBuff(player, context.argument(-2), context.argument(-1), argument, sender)
                        }
                    }
                }
            }
        }
    }

    @CommandBody
    val addEntity = subCommand {
        dynamic {
            dynamic {
                suggestion<ProxyPlayer> { sender, context ->
                    BuffSystem.buffManager.map { it.key }
                }
                dynamic {
                    suggestion<ProxyCommandSender>(uncheck = true) { sender, context ->
                        listOf("{}")
                    }
                    execute<ProxyPlayer> { sender, context, argument ->
                        if (!sender.hasPermission("buff.command.add")) {
                            sender.sendLang("command-no-permission")
                            return@execute
                        }
                        val player = sender.cast<Player>()
                        val entity = player.getEntityRayHit(10.0)
                        if (entity == null) {
                            sender.sendLang("command-valid-entity")
                            return@execute
                        }
                        giveBuff(entity, context.argument(-2), context.argument(-1), argument, sender)
                    }
                }
            }
        }
    }

    @CommandBody
    val remove = subCommand {
        dynamic {
            suggestion<ProxyCommandSender>(uncheck = true) { sender, context ->
                onlinePlayers().map { it.name }
            }
            dynamic {
                suggestion<ProxyCommandSender>(uncheck = true) { sender, context ->
                    buffDataManager[Bukkit.getPlayer(context.argument(-1))?.uniqueId
                        ?: return@suggestion emptyList<String>()]?.map {
                        it.key
                    }
                }
                execute<ProxyCommandSender> { sender, context, argument ->
                    if (!sender.hasPermission("buff.command.remove")) {
                        sender.sendLang("command-no-permission")
                        return@execute
                    }
                    val entity = Bukkit.getPlayer(context.argument(-1)) ?: runCatching {
                        UUID.fromString(context.argument(-1)).livingEntity()
                    }.getOrNull()
                    if (entity == null) {
                        sender.sendLang("command-valid-player", context.argument(-1))
                        return@execute
                    }
                    removeBuff(entity, argument, sender)
                }
            }
        }
    }

    @CommandBody
    val removeEntity = subCommand {
        execute<ProxyPlayer> { sender, context, argument ->
            if (!sender.hasPermission("buff.command.remove")) {
                sender.sendLang("command-no-permission")
                return@execute
            }
            val player = sender.cast<Player>()
            val entity = player.getEntityRayHit(10.0)
            if (entity == null) {
                sender.sendLang("command-valid-entity")
                return@execute
            }
            removeBuff(entity, argument, sender)
        }
    }

    @CommandBody
    val clear = subCommand {
        dynamic(optional = true) {
            suggestion<ProxyCommandSender> { sender, context ->
                onlinePlayers().map { it.name }
            }
            execute<ProxyCommandSender> { sender, context, argument ->
                if (!sender.hasPermission("buff.command.clear")) {
                    sender.sendLang("command-no-permission")
                    return@execute
                }
                val player = Bukkit.getPlayer(argument)
                if (player == null) {
                    sender.sendLang("command-valid-player", argument)
                    return@execute
                }
                clearBuff(player, sender)
            }
        }
        execute<ProxyPlayer> { sender, context, argument ->
            if (!sender.hasPermission("buff.command.clear")) {
                sender.sendLang("command-no-permission")
                return@execute
            }
            val player = sender.cast<Player>()
            val entity = player.getEntityRayHit(10.0)
            if (entity == null) {
                sender.sendLang("command-valid-entity")
                return@execute
            }
            clearBuff(entity, sender)
        }
    }

    @CommandBody
    val info = subCommand {
        dynamic(optional = true) {
            suggestion<ProxyCommandSender> { sender, context ->
                onlinePlayers().map { it.name }
            }
            execute<ProxyCommandSender> { sender, context, argument ->
                if (!sender.hasPermission("buff.command.info")) {
                    sender.sendLang("command-no-permission")
                    return@execute
                }
                val player = Bukkit.getPlayer(argument)
                if (player == null) {
                    sender.sendLang("command-valid-player", argument)
                    return@execute
                }
                info(player, sender)
            }
        }
        execute<ProxyPlayer> { sender, context, argument ->
            if (!sender.hasPermission("buff.command.info")) {
                sender.sendLang("command-no-permission")
                return@execute
            }
            val player = sender.cast<Player>()
            val entity = player.getEntityRayHit(10.0)
            if (entity == null) {
                sender.sendLang("command-valid-entity")
                return@execute
            }
            info(entity, sender)
        }
    }

    private fun info(entity: LivingEntity, sender: ProxyCommandSender) {
        val uuid = entity.uniqueId
        sender.sendLang("info-title", entity.getDisplayName().colored())
        val dataCompound = buffDataManager[uuid]
        if (dataCompound == null) {
            sender.sendLang("info-none")
            return
        }
        val hoverText = sender.asLangText("info-hover-text")
        dataCompound.forEach { (key, data) ->
            val buff = data.buff ?: return
            TellrawJson()
                .append(sender.asLangText("info-format", key, buff.name, buff.status(entity, data)))
                .hoverText(hoverText)
                .runCommand("/buff remove $uuid $key")
                .sendTo(sender)
        }
    }

    private fun giveBuff(entity: LivingEntity, key: String, buffKey: String, json: String, sender: ProxyCommandSender) {
        val buff = BuffSystem.buffManager[buffKey]
        if (buff == null) {
            sender.sendLang("command-valid-buff", buffKey)
            return
        }
        buffDataManager.giveBuff(entity, key, buff, json)
        submit {
            sender.sendLang(
                "command-give-buff", entity.getDisplayName().colored(), key, buffKey,
                buffDataManager[entity.uniqueId]?.get(key)?.let {
                    buff.status(
                        entity,
                        it
                    )
                } ?: kotlin.run {
                    sender.sendLang("command-valid-params")
                }
            )
        }
    }

    private fun removeBuff(entity: LivingEntity, key: String, sender: ProxyCommandSender) {
        buffDataManager.removeBuff(entity, key)
        sender.sendLang("command-remove-buff", entity.getDisplayName().colored(), key)

    }

    private fun clearBuff(entity: LivingEntity, sender: ProxyCommandSender) {
        buffDataManager.clearBuff(entity)
        sender.sendLang("command-clear-buff", entity.getDisplayName().colored())
    }

    @CommandBody
    val reload = subCommand {
        execute<CommandSender> { sender, _, _ ->
            if (!sender.hasPermission("buff.command.reload")) {
                sender.sendLang("command-no-permission")
                return@execute
            }
            BuffSystem.reload()
            sender.sendLang("command-reload")
        }
    }

    @CommandBody
    val json = subCommand {
        execute<CommandSender> { sender, _, _ ->
            if (!sender.hasPermission("buff.command.json")) {
                sender.sendLang("command-no-permission")
                return@execute
            }
            sender.sendLang("command-json")
            sender.sendMessage(*BuffSystem.conditionManager.descriptions().toTypedArray())
        }
    }

    @CommandBody
    val removeIf = subCommand {
        dynamic {
            suggestion<ProxyCommandSender>(uncheck = true) { sender, context ->
                onlinePlayers().map { it.name }
            }
            dynamic {
                suggestion<ProxyCommandSender>(uncheck = true) { sender, context ->
                    listOf("{}")
                }
                execute<ProxyCommandSender> { sender, context, argument ->
                    if (!sender.hasPermission("buff.command.removeif")) {
                        sender.sendLang("command-no-permission")
                        return@execute
                    }
                    val entity = Bukkit.getPlayer(context.argument(-1)) ?: runCatching {
                        UUID.fromString(context.argument(-1)).livingEntity()
                    }.getOrNull()
                    if (entity == null) {
                        sender.sendLang("command-valid-player", context.argument(-1))
                        return@execute
                    }
                    val buffData = buffDataManager[entity.uniqueId] ?: return@execute
                    val condition = argument.parseToMap()
                    buffData.filterValues { data -> condition.all { (key, value) -> data[key.toString()] == value } }
                        .forEach { (key, _) ->
                            removeBuff(entity, key, sender)
                        }
                }
            }
        }
    }
}