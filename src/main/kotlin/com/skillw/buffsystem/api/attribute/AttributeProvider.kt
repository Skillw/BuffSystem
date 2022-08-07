package com.skillw.buffsystem.api.attribute

import com.skillw.buffsystem.BuffSystem.attributeManager
import com.skillw.pouvoir.api.able.Registrable
import org.bukkit.entity.LivingEntity

/**
 * @className AttributeProvider
 * @author Glom
 * @date 2022/8/6 19:34
 * Copyright  2022 user. All rights reserved.
 */
interface AttributeProvider : Registrable<String> {

    fun addAttribute(entity:LivingEntity,key:String,attributes:List<String>)
    fun removeAttribute(entity:LivingEntity,key:String)

    override fun register() {
        attributeManager.register(this)
    }
}