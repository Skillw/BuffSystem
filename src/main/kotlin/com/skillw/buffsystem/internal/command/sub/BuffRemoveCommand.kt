package com.skillw.buffsystem.internal.command.sub

import com.skillw.buffsystem.BuffSystem
import com.skillw.buffsystem.internal.command.BuffCommand.soundClick
import com.skillw.buffsystem.internal.command.BuffCommand.soundFail
import com.skillw.buffsystem.internal.command.BuffCommand.soundSuccess
import com.skillw.buffsystem.util.GsonUtils.parseToMap
import com.skillw.pouvoir.util.EntityUtils.getEntityRayHit
import com.skillw.pouvoir.util.EntityUtils.livingEntity
import com.skillw.pouvoir.util.PlayerUtils.soundFail
import org.bukkit.Bukkit
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.ProxyPlayer
import taboolib.common.platform.command.subCommand
import taboolib.common.platform.function.onlinePlayers
import taboolib.module.chat.colored
import taboolib.module.lang.sendLang
import taboolib.module.nms.getI18nName
import java.util.*

object BuffRemoveCommand {
    private fun removeBuff(entity: LivingEntity, key: String, sender: ProxyCommandSender) {
        BuffSystem.buffDataManager.removeBuff(entity, key)

        sender.soundSuccess()
        sender.sendLang("command-remove-buff", entity.getI18nName().colored(), key)

    }


    val remove = subCommand {
        dynamic {
            suggestion<ProxyCommandSender>(uncheck = true) { sender, _ ->
                sender.soundClick()
                onlinePlayers().map { it.name }
            }
            dynamic {
                suggestion<ProxyCommandSender>(uncheck = true) { sender, context ->
                    sender.soundClick()
                    BuffSystem.buffDataManager[Bukkit.getPlayer(context.argument(-1))?.uniqueId
                        ?: return@suggestion emptyList<String>()]?.map {
                        it.key
                    }
                }
                execute<ProxyCommandSender> { sender, context, argument ->
                    val entity = Bukkit.getPlayer(context.argument(-1)) ?: runCatching {
                        UUID.fromString(context.argument(-1)).livingEntity()
                    }.getOrNull()
                    if (entity == null) {
                        sender.soundFail()
                        sender.sendLang("command-valid-player", context.argument(-1))
                        return@execute
                    }
                    removeBuff(entity, argument, sender)
                }
            }
        }
    }


    val removeEntity = subCommand {
        execute<ProxyPlayer> { sender, _, argument ->
            val player = sender.cast<Player>()
            val entity = player.getEntityRayHit(10.0)
            if (entity == null) {
                player.soundFail()
                sender.sendLang("command-valid-entity")
                return@execute
            }
            removeBuff(entity, argument, sender)
        }
    }


    val removeIf = subCommand {
        dynamic {
            suggestion<ProxyCommandSender>(uncheck = true) { sender, _ ->
                sender.soundClick()
                onlinePlayers().map { it.name }
            }
            dynamic {
                suggestion<ProxyCommandSender>(uncheck = true) { sender, _ ->
                    sender.soundClick()
                    listOf("{}")
                }
                execute<ProxyCommandSender> { sender, context, argument ->
                    val entity = Bukkit.getPlayer(context.argument(-1)) ?: runCatching {
                        UUID.fromString(context.argument(-1)).livingEntity()
                    }.getOrNull()
                    if (entity == null) {
                        sender.soundFail()
                        sender.sendLang("command-valid-player", context.argument(-1))
                        return@execute
                    }
                    val buffData = BuffSystem.buffDataManager[entity.uniqueId] ?: return@execute
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