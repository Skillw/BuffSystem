package com.skillw.buffsystem.internal.feature.compat.attsystem

import com.skillw.attsystem.AttributeSystem
import com.skillw.attsystem.api.AttrAPI.addCompiledData
import com.skillw.attsystem.api.AttrAPI.removeCompiledData
import com.skillw.buffsystem.api.attribute.AttributeProvider
import com.skillw.pouvoir.api.plugin.annotation.AutoRegister
import org.bukkit.entity.LivingEntity

@AutoRegister("com.skillw.attsystem.api.AttrAPI")
object AttributeSystemHook : AttributeProvider {
    override val key: String = "AttributeSystem"
    override fun addAttribute(entity: LivingEntity, source: String, attributes: List<String>) {
        entity.addCompiledData(source, attributes)
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
        source: String,
        attributes: MutableMap<String, Any>,
        conditions: Collection<Any>,
    ) {
        val compiled = AttributeSystem.readManager.readMap(attributes, conditions, entity)
        entity.addCompiledData(source, compiled)
    }

    override fun removeAttribute(entity: LivingEntity, source: String) {
        entity.removeCompiledData(source)
    }
}