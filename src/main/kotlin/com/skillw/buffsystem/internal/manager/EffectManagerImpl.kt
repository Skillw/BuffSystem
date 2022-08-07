package com.skillw.buffsystem.internal.manager

import com.skillw.buffsystem.BuffSystem
import com.skillw.buffsystem.api.event.EffectLoadEvent
import com.skillw.buffsystem.api.manager.EffectManager
import com.skillw.pouvoir.util.FileUtils.loadYamls
import java.io.File

object EffectManagerImpl : EffectManager() {
    override val key = "EffectManager"
    override val priority = 3
    override val subPouvoir = BuffSystem

    override fun onEnable() {
        onReload()
    }

    override fun onReload() {
        //开始加载
        this.entries.filter { it.value.config }.forEach { this.remove(it.key) }
        File(BuffSystem.plugin.dataFolder, "effects").loadYamls()
            .forEach { yaml ->
                for (key in yaml.getKeys(false)) {
                    val section = yaml.getConfigurationSection(key) ?: continue
                    val event = EffectLoadEvent(section)
                    event.call()
                    event.result?.also { it.config = true; it.register(); }
                }
            }

    }


}