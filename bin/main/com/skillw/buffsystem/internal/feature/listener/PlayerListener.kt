package com.skillw.buffsystem.internal.feature.listener

import com.skillw.buffsystem.BuffSystem
import com.skillw.buffsystem.api.data.BuffDataCompound
import com.skillw.buffsystem.internal.feature.compat.pouvoir.container.BuffContainer
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
        val data = BuffContainer[name, "buff-data"]
        if (data != null) buffDataCompound = BuffDataCompound.deserialize(uuid, data)
        if (buffDataCompound == null) buffDataCompound = BuffDataCompound(uuid)
        buffDataCompound.register()
        BuffContainer[name, "buff-data"] = "null"
        player.activePotionEffects.forEach {
            player.removePotionEffect(it.type)
        }
    }


    @SubscribeEvent
    fun onPlayerLeft(event: PlayerQuitEvent) {
        val player = event.player
        val uuid = player.uniqueId
        val name = player.name
        val dataCompound = BuffSystem.buffDataManager[uuid] ?: return
        dataCompound.clear()
        player.activePotionEffects.forEach {
            player.removePotionEffect(it.type)
        }
        BuffContainer[name, "buff-data"] = dataCompound.serialize()
        BuffSystem.buffDataManager.remove(uuid)
    }


}