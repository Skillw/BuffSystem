package com.skillw.buffsystem.internal.effect

import com.skillw.buffsystem.api.data.BuffData
import com.skillw.buffsystem.api.effect.BaseEffect
import com.skillw.buffsystem.api.manager.AttributeManager.attrProvider
import com.skillw.buffsystem.internal.feature.compat.attsystem.AttributeSystemHook
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.entity.LivingEntity

class AttributeEffect(key: String, val attributes: Any, val conditions: Map<String, Any> = emptyMap()) :
    BaseEffect(key),
    ConfigurationSerializable {


    private fun getSource(data: BuffData): String {
        return "attribute-effect-$key-source-${data.key}}"
    }

    override fun realize(entity: LivingEntity, data: BuffData) {
        with(data) {
            val attributes = attributes.handle()
            if (attrProvider is AttributeSystemHook && attributes is Map<*, *>) {
                attributes as MutableMap<String, Any>
                val conditions = conditions.handle()
                (attrProvider as AttributeSystemHook).addAttribute(entity, getSource(data), attributes, conditions)
            } else {
                attributes as List<String>
                attrProvider.addAttribute(entity, getSource(data), attributes)
            }
        }
    }

    override fun unrealize(entity: LivingEntity, data: BuffData) {
        attrProvider.removeAttribute(entity, getSource(data))
    }


    override fun serialize(): MutableMap<String, Any> {
        return linkedMapOf("type" to "attribute", "attributes" to attributes)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AttributeEffect

        if (key != other.key) return false
        if (attributes != other.attributes) return false

        return true
    }

    override fun hashCode(): Int {
        var result = key.hashCode()
        result = 31 * result + attributes.hashCode()
        return result
    }

}