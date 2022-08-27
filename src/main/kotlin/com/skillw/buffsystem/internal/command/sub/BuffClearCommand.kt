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
import taboolib.module.chat.colored
import taboolib.module.lang.sendLang

object BuffClearCommand {


    val clear = subCommand {
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
                clearBuff(player, sender)
            }
        }
        execute<ProxyPlayer> { sender, context, argument ->
            val player = sender.cast<Player>()
            val entity = player.getEntityRayHit(10.0)
            if (entity == null) {
                sender.sendLang("command-valid-entity")
                return@execute
            }
            clearBuff(entity, sender)
        }
    }

    private fun clearBuff(entity: LivingEntity, sender: ProxyCommandSender) {
        BuffSystem.buffDataManager.clearBuff(entity)
        sender.sendLang("command-clear-buff", entity.getDisplayName().colored())
    }
}