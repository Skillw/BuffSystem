package com.skillw.buffsystem.internal.feature.compat.attplus

import com.skillw.buffsystem.api.attribute.AttributeProvider
import com.skillw.pouvoir.api.annotation.AutoRegister
import org.bukkit.entity.LivingEntity
import org.serverct.ersha.jd.api.EntityAttributeAPI

@AutoRegister("org.serverct.ersha.jd.api.EntityAttributeAPI")
object AttributePlusIIHook : AttributeProvider {
    override val key: String = "AttributePlusII"
    override fun addAttribute(entity: LivingEntity, key: String, attributes: List<String>) {
        EntityAttributeAPI.addEntityAttribute(entity, key, attributes)
    }

    override fun removeAttribute(entity: LivingEntity, key: String) {
        EntityAttributeAPI.removeEntityAttribute(entity, key)
    }
}