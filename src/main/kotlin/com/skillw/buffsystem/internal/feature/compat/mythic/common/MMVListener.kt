package com.skillw.buffsystem.internal.feature.compat.mythic.common

import io.lumine.mythic.bukkit.events.MythicMechanicLoadEvent
import taboolib.common.platform.event.OptionalEvent
import taboolib.common.platform.event.SubscribeEvent

object MMVListener {
    @SubscribeEvent(bind = "io.lumine.mythic.bukkit.events.MythicMechanicLoadEvent")
    fun onMythicMechanicLoad(optionalEvent: OptionalEvent) {
        val event = optionalEvent.get<MythicMechanicLoadEvent>()
        if (event.mechanicName.lowercase() in arrayOf("buff-sys", "buffsys")) {
            event.register(BuffMechanicV(event.config))
        }
    }
}