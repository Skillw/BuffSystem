package com.skillw.buffsystem.internal.feature.compat.attplus

import com.skillw.buffsystem.api.attribute.AttributeProvider
import com.skillw.pouvoir.api.plugin.annotation.AutoRegister
import org.bukkit.entity.LivingEntity
import org.serverct.ersha.api.AttributeAPI

@AutoRegister("org.serverct.ersha.api.AttributeAPI")
object AttributePlusIIIHook : AttributeProvider {
    override val key: String = "AttributePlusIII"
    override fun addAttribute(entity: LivingEntity, source: String, attributes: List<String>) {
        val data = AttributeAPI.getAttrData(entity)
        AttributeAPI.addSourceAttribute(data, source, attributes)
    }

    override fun removeAttribute(entity: LivingEntity, source: String) {
        val data = AttributeAPI.getAttrData(entity)
        AttributeAPI.takeSourceAttribute(data, source)
    }
}