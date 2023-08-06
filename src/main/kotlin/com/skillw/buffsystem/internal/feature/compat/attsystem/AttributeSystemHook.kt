package com.skillw.buffsystem.internal.feature.compat.attsystem

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.AttrAPI.addCompiledData
import com.skillw.attsystem.api.AttrAPI.removeCompiledData
import com.skillw.buffsystem.api.attribute.AttributeProvider
import com.skillw.pouvoir.api.annotation.AutoRegister
import org.bukkit.entity.LivingEntity

@AutoRegister("com.skillw.attsystem.api.AttrAPI")
object AttributeSystemHook : AttributeProvider {
    override val key: String = "AttributeSystem"
    override fun addAttribute(entity: LivingEntity, source: String, attributes: List<String>) {
        entity.addCompiledData(source, attributes)
    }

    fun addAttribute(
        entity: LivingEntity,
        source: String,
        attributes: MutableMap<String, Any>,
        conditions: Collection<Any>,
    ) {
        val compiled = AttributeSystem.readManager.readMap(attributes, conditions, entity, null)
        AttributeSystem.compiledAttrDataManager.addCompiledData(entity, source, compiled)
    }

    override fun removeAttribute(entity: LivingEntity, source: String) {
        entity.removeCompiledData(source)
    }
}