package com.skillw.buffsystem.internal.listener

import com.skillw.buffsystem.BuffSystem
import com.skillw.buffsystem.api.data.BuffDataCompound
import com.skillw.pouvoir.Pouvoir
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.platform.event.SubscribeEvent


object PlayerListener {


    @SubscribeEvent
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        val uuid = player.uniqueId
        val name = player.name
        var buffDataCompound: BuffDataCompound? = null
        val data = Pouvoir.containerManager[name, "buff-data"]
        if (data != null) buffDataCompound = BuffDataCompound.deserialize(uuid, data)
        if (buffDataCompound == null) buffDataCompound = BuffDataCompound(uuid)
        buffDataCompound.register()
        Pouvoir.containerManager[name, "buff-data"] = "null"
    }


    @SubscribeEvent
    fun onPlayerLeft(event: PlayerQuitEvent) {
        val player = event.player
        val uuid = player.uniqueId
        val name = player.name
        val buffDataCompound = BuffSystem.buffDataManager[uuid] ?: return
        buffDataCompound.unrealize()
        player.activePotionEffects.forEach {
            player.removePotionEffect(it.type)
        }
        Pouvoir.containerManager[name, "buff-data"] = buffDataCompound.serialize()
        BuffSystem.buffDataManager.remove(uuid)
    }

}