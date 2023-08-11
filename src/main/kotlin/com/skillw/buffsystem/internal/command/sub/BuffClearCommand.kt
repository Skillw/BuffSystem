package com.skillw.buffsystem.internal.command.sub

import com.skillw.buffsystem.BuffSystem
import com.skillw.buffsystem.internal.command.BuffCommand.soundClick
import com.skillw.buffsystem.internal.command.BuffCommand.soundFail
import com.skillw.pouvoir.util.getEntityRayHit
import com.skillw.pouvoir.util.soundFail
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

object BuffClearCommand {


    val clear = subCommand {
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
                clearBuff(player, sender)
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
            clearBuff(entity, sender)
        }
    }

    private fun clearBuff(entity: LivingEntity, sender: ProxyCommandSender) {
        BuffSystem.buffDataManager.clearBuff(entity)
        sender.sendLang("command-clear-buff", entity.getI18nName().colored())
    }
}