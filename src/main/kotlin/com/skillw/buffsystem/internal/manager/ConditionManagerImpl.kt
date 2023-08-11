package com.skillw.buffsystem.internal.manager

import com.skillw.buffsystem.BuffSystem
import com.skillw.buffsystem.api.manager.ConditionManager
import com.skillw.buffsystem.internal.core.condition.ScriptBuffCondition
import com.skillw.pouvoir.util.loadMultiply
import java.io.File

object ConditionManagerImpl : ConditionManager() {
    override val key = "ConditionManager"
    override val priority = 2
    override val subPouvoir = BuffSystem

    override fun onEnable() {
        onReload()
    }

    override fun descriptions(): List<String> {
        return map { it.value.description }
    }

    override fun onReload() {
        this
            .filter { it.value is ScriptBuffCondition }
            .forEach {
                removeByValue(it.value)
            }
        loadMultiply(File(BuffSystem.plugin.dataFolder, "conditions"), ScriptBuffCondition::class.java)
            .forEach {
                it.key.register()
            }
    }

}