package com.skillw.buffsystem.internal.core.condition

import com.skillw.buffsystem.api.condition.BuffCondition
import com.skillw.buffsystem.api.data.BuffData
import com.skillw.pouvoir.Pouvoir
import com.skillw.pouvoir.api.script.PouFileCompiledScript
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.entity.LivingEntity

/**
 * @className ScriptBuffCondition
 *
 * @author Glom
 * @date 2022/7/18 20:18 Copyright 2022 user. All rights reserved.
 */
class ScriptBuffCondition(
    override val key: String,
    private val script: PouFileCompiledScript,
    override val description: String = "",
) : BuffCondition, ConfigurationSerializable {
    override fun status(entity: LivingEntity, data: BuffData): String {
        return Pouvoir.scriptManager.invoke<String>(
            script, "status", parameters = arrayOf(entity, data)
        ).toString()
    }

    override fun init(entity: LivingEntity, data: BuffData) {
        Pouvoir.scriptManager.invoke<Unit>(
            script, "init", parameters = arrayOf(entity, data)
        )
    }

    override fun isDeleted(entity: LivingEntity, data: BuffData): Boolean {
        return Pouvoir.scriptManager.invoke(
            script, "isDeleted", parameters = arrayOf(entity, data)
        ) as? Boolean? ?: false
    }

    override fun test(entity: LivingEntity, data: BuffData): Boolean {
        return Pouvoir.scriptManager.invoke(
            script, "test", parameters = arrayOf(entity, data)
        ) as? Boolean? ?: true
    }


    override fun serialize(): MutableMap<String, Any> {
        return linkedMapOf()
    }
}