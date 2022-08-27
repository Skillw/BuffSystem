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
import taboolib.module.chat.TellrawJson
import taboolib.module.chat.colored
import taboolib.module.lang.asLangText
import taboolib.module.lang.sendLang

object BuffInfoCommand {

    private fun info(entity: LivingEntity, sender: ProxyCommandSender) {
        val uuid = entity.uniqueId
        sender.sendLang("info-title", entity.getDisplayName().colored())
        val dataCompound = BuffSystem.buffDataManager[uuid]
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

    val info = subCommand {
        dynamic(optional = true) {
            suggestion<ProxyCommandSender> { sender, context ->
                onlinePlayers().map { it.name }
            }
            execute<ProxyCommandSender> { sender, context, argument ->
                val player = Bukkit.getPlayer(argument)
                if (player == null) {
                    sender.sendLang("command-valid-player", argument)
                    return@execute
                }
                info(player, sender)
            }
        }
        execute<ProxyPlayer> { sender, context, argument ->
            val player = sender.cast<Player>()
            val entity = player.getEntityRayHit(10.0)
            if (entity == null) {
                sender.sendLang("command-valid-entity")
                return@execute
            }
            info(entity, sender)
        }
    }
}