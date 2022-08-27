package com.skillw.buffsystem.internal.feature.listener

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent
import com.skillw.buffsystem.BuffSystem
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDeathEvent
import taboolib.common.platform.event.OptionalEvent
import taboolib.common.platform.event.SubscribeEvent

object EntityListener {

    @SubscribeEvent
    fun onEntityDead(event: EntityDeathEvent) {
        val livingEntity = event.entity
        if (livingEntity !is Player) {
            BuffSystem.buffDataManager.remove(livingEntity.uniqueId)
        }
    }

    @SubscribeEvent(bind = "com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent")
    fun onEntityDead(optionalEvent: OptionalEvent) {
        val event = optionalEvent.get<EntityRemoveFromWorldEvent>()
        val livingEntity = event.entity
        if (livingEntity !is Player)
            BuffSystem.buffDataManager.remove(livingEntity.uniqueId)
    }

}
