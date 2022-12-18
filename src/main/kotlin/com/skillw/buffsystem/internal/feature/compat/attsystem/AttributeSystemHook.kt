package com.skillw.buffsystem.internal.feature.compat.attsystem

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.AttrAPI.addAttribute
import com.skillw.attsystem.api.AttrAPI.removeAttribute
import com.skillw.attsystem.api.attribute.compound.AttributeData
import com.skillw.buffsystem.api.attribute.AttributeProvider
import com.skillw.pouvoir.api.annotation.AutoRegister
import org.bukkit.entity.LivingEntity

@AutoRegister("com.skillw.attsystem.api.AttrAPI")
object AttributeSystemHook : AttributeProvider {
    override val key: String = "AttributeSystem"
    override fun addAttribute(entity: LivingEntity, source: String, attributes: List<String>) {
        entity.addAttribute(source, attributes, false)
    }

    private fun MutableMap<String, Any>.removeDeep(path: String) {
        val splits = path.split(".")
        if (splits.isEmpty()) {
            this.remove(path)
            return
        }
        var compound = this
        var temp: MutableMap<String, Any>
        for (node in splits) {
            if (node.equals(splits.last(), ignoreCase = true)) {
                compound.remove(node)
            }
            compound[node].also { temp = ((it as MutableMap<String, Any>?) ?: return) }
            compound = temp
        }
    }

    fun addAttribute(
        entity: LivingEntity,
        key: String,
        attributes: MutableMap<String, Any>,
        conditions: MutableMap<String, Any>,
    ) {
        AttributeSystem.conditionManager.conditionNBT("buff", entity, conditions).forEach {
            attributes.removeDeep(it)
        }
        val attributeData = AttributeData.fromMap(attributes)
        entity.addAttribute(key, attributeData)
    }

    override fun removeAttribute(entity: LivingEntity, source: String) {
        entity.removeAttribute(source)
    }
}