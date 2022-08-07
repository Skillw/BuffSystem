package com.skillw.buffsystem.internal.hook.oa

import ac.github.oa.api.OriginAttributeAPI
import ac.github.oa.internal.core.attribute.AttributeData
import com.skillw.attsystem.api.AttrAPI.addAttribute
import com.skillw.attsystem.api.AttrAPI.removeAttribute
import com.skillw.buffsystem.api.attribute.AttributeProvider
import com.skillw.pouvoir.api.annotation.AutoRegister
import org.bukkit.entity.LivingEntity

@AutoRegister("ac.github.oa.api.OriginAttributeAPI")
object OriginAttributeHook : AttributeProvider {
    override val key: String = "OriginAttribute"
    override fun addAttribute(entity: LivingEntity, key: String, attributes: List<String>) {
        OriginAttributeAPI.setExtra(entity.uniqueId, key, OriginAttributeAPI.loadList(attributes))
    }

    override fun removeAttribute(entity: LivingEntity, key: String) {
        OriginAttributeAPI.removeExtra(entity.uniqueId, key)
    }
}