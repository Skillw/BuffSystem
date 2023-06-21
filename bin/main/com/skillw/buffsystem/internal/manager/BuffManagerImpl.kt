package com.skillw.buffsystem.internal.manager

import com.skillw.buffsystem.BuffSystem
import com.skillw.buffsystem.api.buff.Buff
import com.skillw.buffsystem.api.manager.BuffManager
import com.skillw.pouvoir.util.loadMultiply
import java.io.File

object BuffManagerImpl : BuffManager() {
    override val key = "BuffManager"
    override val priority = 1
    override val subPouvoir = BuffSystem

    override fun onEnable() {
        onReload()
    }

    override fun onReload() {
        //开始加载
        this.entries.filter { it.value.config }.forEach { this.remove(it.key) }
        loadMultiply(File(BuffSystem.plugin.dataFolder, "buffs"), Buff::class.java).forEach {
            BuffSystem.buffManager.register(it.key)
        }

    }


}