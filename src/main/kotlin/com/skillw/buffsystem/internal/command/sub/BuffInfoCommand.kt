package com.skillw.buffsystem.internal.command.sub

import com.skillw.buffsystem.BuffSystem
import com.skillw.buffsystem.internal.command.BuffCommand.soundClick
import com.skillw.buffsystem.internal.command.BuffCommand.soundFail
import com.skillw.buffsystem.internal.command.BuffCommand.soundSuccess
import com.skillw.pouvoir.util.getEntityRayHit
import com.skillw.pouvoir.util.soundFail
import org.bukkit.Bukkit
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.ProxyPlayer
import taboolib.common.platform.command.subCommand
import taboolib.common.platform.function.onlinePlayers
import taboolib.module.chat.TellrawJson
import taboolib.module.chat.colored
import taboolib.module.lang.asLangText
import taboolib.module.lang.sendLang
import taboolib.module.nms.getI18nName

object BuffInfoCommand {

    private fun info(entity: LivingEntity, sender: ProxyCommandSender) {
        val uuid = entity.uniqueId
        sender.sendLang("info-title", entity.getI18nName().colored())
        val dataCompound = BuffSystem.buffDataManager[uuid]
        if (dataCompound == null) {
            sender.soundFail()
            sender.sendLang("info-none")
            return
        }
        sender.soundSuccess()
        val details = sender.asLangText("info-details")
        val remove = sender.asLangText("info-click-to-remove")
        val removeHover = sender.asLangText("info-click-to-remove-hover-text")

        dataCompound.forEach { (key, data) ->
            val buff = data.buff ?: return@forEach

            TellrawJson()
                .append(sender.asLangText("info-format", key, buff.display))
                .append(
                    TellrawJson()
                        .append(details)
                        .hoverText(buff.description(data, entity).joinToString("\n"))
                ).append(
                    TellrawJson()
                        .append(remove)
                        .hoverText(removeHover)
                        .runCommand("/buff remove $key")
                )
                .sendTo(sender)
        }
    }

    val info = subCommand {
        dynamic(optional = true) {
            suggestion<ProxyCommandSender> { sender, _ ->
                sender.soundClick()
                onlinePlayers().map { it.name }
            }
            execute<ProxyCommandSender> { sender, _, argument ->
                val player = Bukkit.getPlayer(argument)
                if (player == null) {
                    sender.soundFail()
                    sender.sendLang("command-valid-player", argument)
                    return@execute
                }
                info(player, sender)
            }
        }
        execute<ProxyPlayer> { sender, _, _ ->
            val player = sender.cast<Player>()
            val entity = player.getEntityRayHit(10.0) as? LivingEntity?
            if (entity == null) {
                player.soundFail()
                sender.sendLang("command-valid-entity")
                return@execute
            }
            info(entity, sender)
        }
    }
}