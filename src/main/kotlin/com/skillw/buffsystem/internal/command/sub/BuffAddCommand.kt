package com.skillw.buffsystem.internal.command.sub

import com.skillw.buffsystem.BuffSystem
import com.skillw.pouvoir.util.EntityUtils.getDisplayName
import com.skillw.pouvoir.util.EntityUtils.getEntityRayHit
import org.bukkit.Bukkit
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.ProxyPlayer
import taboolib.common.platform.command.subCommand
import taboolib.common.platform.function.onlinePlayers
import taboolib.common.platform.function.submitAsync
import taboolib.module.chat.colored
import taboolib.module.lang.sendLang

object BuffAddCommand {
    private fun giveBuff(entity: LivingEntity, key: String, buffKey: String, json: String, sender: ProxyCommandSender) {
        val buff = BuffSystem.buffManager[buffKey]
        if (buff == null) {
            sender.sendLang("command-valid-buff", buffKey)
            return
        }
        BuffSystem.buffDataManager.giveBuff(entity, key, buff, json)
        submitAsync {
            sender.sendLang(
                "command-give-buff", entity.getDisplayName().colored(), key, buffKey,
                BuffSystem.buffDataManager[entity.uniqueId]?.get(key)?.let {
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
}