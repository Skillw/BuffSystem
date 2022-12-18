package com.skillw.buffsystem.api.attribute

import com.skillw.buffsystem.BuffSystem.attributeManager
import com.skillw.pouvoir.api.able.Registrable
import org.bukkit.entity.LivingEntity

/**
 * @className AttributeProvider
 *
 * @author Glom
 * @date 2022/8/6 19:34 Copyright 2022 user. All rights reserved.
 */
interface AttributeProvider : Registrable<String> {

    /**
     * 给属性
     *
     * @param entity 实体
     * @param source 属性源
     * @param attributes 属性
     */
    fun addAttribute(entity: LivingEntity, source: String, attributes: List<String>)

    /**
     * 删属性
     *
     * @param entity 实体
     * @param source 属性源
     */
    fun removeAttribute(entity: LivingEntity, source: String)

    override fun register() {
        attributeManager.register(this)
    }

}