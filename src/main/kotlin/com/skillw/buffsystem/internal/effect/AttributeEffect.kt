package com.skillw.buffsystem.internal.effect

import com.skillw.buffsystem.BuffSystem
import com.skillw.buffsystem.api.data.BuffData
import com.skillw.buffsystem.api.effect.BaseEffect
import com.skillw.buffsystem.api.event.EffectLoadEvent
import com.skillw.buffsystem.api.manager.AttributeManager.attrProvider
import com.skillw.pouvoir.Pouvoir
import com.skillw.pouvoir.api.PouvoirAPI.placeholder
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.util.unsafeLazy
import taboolib.platform.compat.replacePlaceholder
import java.util.*

class AttributeEffect(override val key: String, val attributes: List<String>) : BaseEffect(),
    ConfigurationSerializable {

    //Key to get the source from BuffMemory
    private val sourceKey = "attribute-effect-$key-source"

    private fun getSource(data: BuffData): String {
        if (!data.containsKey(sourceKey))
            data[sourceKey] = "${data.buffKey}:$key:${UUID.randomUUID()}"
        return data.getAs<String>(sourceKey).toString()
    }

    override fun realize(entity: LivingEntity, data: BuffData) {
        var attributes = data.replace(this.attributes)
        if (entity is Player) {
            attributes = attributes.map { it.replacePlaceholder(entity) }
        }
        attributes = attributes.map { it.placeholder(entity) }
        attrProvider.addAttribute(entity, getSource(data), attributes)
    }

    override fun unrealize(entity: LivingEntity, data: BuffData) {
        attrProvider.removeAttribute(entity, getSource(data))
    }

    companion object {

        @JvmStatic
        fun deserialize(section: ConfigurationSection): AttributeEffect? {
            try {
                val key = section.name
                if (section["type"].toString().lowercase() != "attribute") return null
                val attributes = section.getStringList("attributes")
                return AttributeEffect(key, attributes).apply { config = true }
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