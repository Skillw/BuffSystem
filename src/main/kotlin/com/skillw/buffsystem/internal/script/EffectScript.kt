package com.skillw.buffsystem.internal.script

import com.skillw.buffsystem.api.data.BuffData
import com.skillw.pouvoir.Pouvoir
import com.skillw.pouvoir.api.able.Registrable
import com.skillw.pouvoir.api.map.LowerKeyMap
import com.skillw.pouvoir.internal.script.common.PouCompiledScript
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.entity.LivingEntity

class EffectScript(
    override val key: String,
    private val script: PouCompiledScript,
) : Registrable<String>, ConfigurationSerializable {
    companion object {
        val effectScripts = LowerKeyMap<EffectScript>()
    }

    override fun serialize(): MutableMap<String, Any> {
        return linkedMapOf()
    }

    fun realize(entity: LivingEntity, data: BuffData, section: ConfigurationSection) {
        Pouvoir.scriptManager.invoke<Unit>(
            script, "realize",
            parameters = arrayOf(entity,data,section)
        )
    }

    fun unrealize(entity: LivingEntity, data: BuffData, section: ConfigurationSection) {
        Pouvoir.scriptManager.invoke<Unit>(
            script, "unrealize",
            parameters = arrayOf(entity,data,section)
        )
    }

    override fun register() {
        effectScripts.register(this)
    }
}