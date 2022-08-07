package com.skillw.buffsystem.internal.effect

import com.skillw.buffsystem.api.data.BuffData
import com.skillw.buffsystem.api.effect.BaseEffect
import com.skillw.buffsystem.api.event.EffectLoadEvent
import com.skillw.buffsystem.internal.script.EffectScript
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.entity.LivingEntity
import taboolib.common.platform.event.SubscribeEvent

class ScriptEffect(
    override val key: String,
    val effectScript: EffectScript,
    private val section: ConfigurationSection,
) : BaseEffect(), ConfigurationSerializable {

    companion object {
        @JvmStatic
        fun deserialize(section: ConfigurationSection): ScriptEffect? {
            try {
                val key = section.name
                val effectScript = EffectScript.effectScripts[section["type"].toString()] ?: return null
                return ScriptEffect(key, effectScript, section).apply { config = true }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }

        @SubscribeEvent
        fun load(event: EffectLoadEvent) {
            event.result = deserialize(event.section) ?: return
        }
    }

    override fun serialize(): MutableMap<String, Any> {
        return linkedMapOf()
    }

    override fun realize(entity: LivingEntity, data: BuffData) {
        effectScript.realize(entity, data, section)
    }

    override fun unrealize(entity: LivingEntity, data: BuffData) {
        effectScript.unrealize(entity, data, section)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ScriptEffect) return false
        if (!super.equals(other)) return false

        if (key != other.key) return false
        if (effectScript != other.effectScript) return false
        if (section != other.section) return false

        return true
    }

    override fun hashCode(): Int {
        var result = key.hashCode()
        result = 31 * result + effectScript.hashCode()
        result = 31 * result + section.hashCode()
        return result
    }


}