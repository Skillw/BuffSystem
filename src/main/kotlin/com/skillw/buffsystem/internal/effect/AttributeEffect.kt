package com.skillw.buffsystem.internal.effect

import com.skillw.buffsystem.api.data.BuffData
import com.skillw.buffsystem.api.effect.BaseEffect
import com.skillw.buffsystem.api.manager.AttributeManager.attrProvider
import com.skillw.pouvoir.api.PouvoirAPI.placeholder
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import taboolib.platform.compat.replacePlaceholder
import java.util.*

class AttributeEffect(key: String, val attributes: List<String>) : BaseEffect(key),
    ConfigurationSerializable {


    private val sourceKey = "attribute-effect-$key-source-${UUID.randomUUID()}"
    private fun getSource(data: BuffData): String {
        return data.getOrPut(sourceKey) { sourceKey }.toString()
    }

    override fun realize(entity: LivingEntity, data: BuffData) {
        var attributes = data.handle(this.attributes)
        if (entity is Player) {
            attributes = attributes.map { it.replacePlaceholder(entity) }
        }
        attributes = attributes.map { it.placeholder(entity) }
        attrProvider.addAttribute(entity, getSource(data), attributes)
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