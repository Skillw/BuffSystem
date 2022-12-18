package com.skillw.buffsystem.internal.command.sub

import com.skillw.buffsystem.BuffSystem
import com.skillw.buffsystem.internal.command.BuffCommand.soundClick
import com.skillw.buffsystem.internal.command.BuffCommand.soundFail
import com.skillw.buffsystem.internal.command.BuffCommand.soundSuccess
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
import taboolib.module.nms.getI18nName

object BuffAddCommand {
    private fun giveBuff(entity: LivingEntity, key: String, buffKey: String, json: String, sender: ProxyCommandSender) {
        val buff = BuffSystem.buffManager[buffKey]
        if (buff == null) {
            sender.soundFail()
            sender.sendLang("command-valid-buff", buffKey)
            return
        }
        BuffSystem.buffDataManager.giveBuff(entity, key, buff, json)
        submitAsync {
            sender.soundSuccess()
            sender.sendLang(
                "command-give-buff", entity.getI18nName().colored(), key, buffKey,
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
            suggestion<ProxyCommandSender> { sender, _ ->
                sender.soundClick()
                onlinePlayers().map { it.name }
            }
            dynamic {
                suggestion<ProxyCommandSender> { sender, _ ->
                    sender.soundClick()
                    listOf("key")
                }
                dynamic {
                    suggestion<ProxyCommandSender> { sender, _ ->
                        sender.soundClick()
                        BuffSystem.buffManager.map { it.key }
                    }
                    dynamic {
                        suggestion<ProxyCommandSender>(uncheck = true) { sender, _ ->
                            sender.soundClick()
                            listOf("{}")
                        }
                        execute<ProxyCommandSender> { sender, context, argument ->
                            val player = Bukkit.getPlayer(context.argument(-3))
                            if (player == null) {
                                sender.soundFail()
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
            suggestion<ProxyCommandSender> { sender, _ ->
                sender.soundClick()
                listOf("key")
            }
            dynamic {
                suggestion<ProxyPlayer> { sender, _ ->
                    sender.soundClick()
                    BuffSystem.buffManager.map { it.key }
                }
                dynamic {
                    suggestion<ProxyCommandSender>(uncheck = true) { sender, _ ->
                        sender.soundClick()
                        listOf("{}")
                    }
                    execute<ProxyPlayer> { sender, context, argument ->
                        val player = sender.cast<Player>()
                        val entity = player.getEntityRayHit(10.0)
                        if (entity == null) {
                            sender.soundFail()
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