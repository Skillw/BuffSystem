package com.skillw.buffsystem.internal.command.sub

import com.skillw.buffsystem.BuffSystem
import com.skillw.buffsystem.internal.command.BuffCommand.soundClick
import com.skillw.buffsystem.internal.command.BuffCommand.soundFail
import com.skillw.buffsystem.internal.command.BuffCommand.soundSuccess
import com.skillw.pouvoir.util.EntityUtils.getEntityRayHit
import com.skillw.pouvoir.util.PlayerUtils.soundFail
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
        val hoverText = sender.asLangText("info-hover-text")
        dataCompound.forEach { (key, data) ->
            val buff = data.buff
            TellrawJson()
                .append(sender.asLangText("info-format", key, buff.name, buff.status(entity, data)))
                .hoverText(hoverText)
                .runCommand("/buff remove $uuid $key")
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
            val entity = player.getEntityRayHit(10.0)
            if (entity == null) {
                player.soundFail()
                sender.sendLang("command-valid-entity")
                return@execute
            }
            info(entity, sender)
        }
    }
}