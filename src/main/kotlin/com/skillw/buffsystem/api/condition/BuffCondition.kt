package com.skillw.buffsystem.api.condition

import com.skillw.buffsystem.BuffSystem
import com.skillw.buffsystem.api.data.BuffData
import com.skillw.pouvoir.api.able.Registrable
import org.bukkit.entity.LivingEntity

/**
 * @className BuffCondition
 *
 * @author Glom
 * @date 2022/7/18 7:52 Copyright 2022 user. All rights reserved.
 */
interface BuffCondition : Registrable<String> {
    /** Description */// /buff json
    val description: String

    /**
     * Status
     *
     * @param entity
     * @param data
     * @return
     */// /buff info
    fun status(entity: LivingEntity, data: BuffData): String

    /**
     * Init
     *
     * @param entity
     * @param data
     */// First buff data init
    fun init(entity: LivingEntity, data: BuffData)

    /**
     * Is deleted
     *
     * @param entity
     * @param data
     * @return
     */// Whether delete the buff data or not (depends on previous)
    fun isDeleted(entity: LivingEntity, data: BuffData): Boolean

    /**
     * Test
     *
     * @param entity
     * @param data
     * @return
     */// The main function, which is used to predicate whether an entity's buff takes effect or not
    fun test(entity: LivingEntity, data: BuffData): Boolean
    override fun register() {
        BuffSystem.conditionManager.register(key, this)
    }
}