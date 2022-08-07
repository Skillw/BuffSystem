package com.skillw.buffsystem.internal.hook.attsystem

import com.skillw.attsystem.api.AttrAPI.addAttribute
import com.skillw.attsystem.api.AttrAPI.removeAttribute
import com.skillw.buffsystem.api.attribute.AttributeProvider
import com.skillw.pouvoir.api.annotation.AutoRegister
import org.bukkit.entity.LivingEntity

@AutoRegister("com.skillw.attsystem.api.AttrAPI")
object AttributeSystemHook : AttributeProvider {
    override val key: String = "AttributeSystem"
    override fun addAttribute(entity: LivingEntity, key: String, attributes: List<String>) {
        entity.addAttribute(key,attributes,false)
    }

    override fun removeAttribute(entity: LivingEntity, key: String) {
        entity.removeAttribute(key)
    }
}