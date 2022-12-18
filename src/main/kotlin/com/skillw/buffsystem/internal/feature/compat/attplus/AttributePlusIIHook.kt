package com.skillw.buffsystem.internal.feature.compat.attplus

import com.skillw.buffsystem.api.attribute.AttributeProvider
import com.skillw.pouvoir.api.annotation.AutoRegister
import org.bukkit.entity.LivingEntity
import org.serverct.ersha.jd.api.EntityAttributeAPI

@AutoRegister("org.serverct.ersha.jd.api.EntityAttributeAPI")
object AttributePlusIIHook : AttributeProvider {
    override val key: String = "AttributePlusII"
    override fun addAttribute(entity: LivingEntity, source: String, attributes: List<String>) {
        EntityAttributeAPI.addEntityAttribute(entity, source, attributes)
    }

    override fun removeAttribute(entity: LivingEntity, source: String) {
        EntityAttributeAPI.removeEntityAttribute(entity, source)
    }
}