package com.skillw.buffsystem.internal.feature.compat.mythic

import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMechanicLoadEvent
import taboolib.common.platform.event.OptionalEvent
import taboolib.common.platform.event.SubscribeEvent

object MMIVListener {
    @SubscribeEvent(bind = "io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMechanicLoadEvent")
    fun onMythicMechanicLoad(optionalEvent: OptionalEvent) {
        val event = optionalEvent.get<MythicMechanicLoadEvent>()
        if (event.mechanicName.lowercase() in arrayOf("buff-sys", "buffsys")) {
            event.register(BuffMechanicIV(event.config.line, event.config))
        }
    }
}