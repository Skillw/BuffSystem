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
     * 条件状态（给玩家看的）
     *
     * @param entity 实体
     * @param data buff数据
     * @return 条件状态
     */// /buff info
    fun status(entity: LivingEntity, data: BuffData): String

    /**
     * 初始化条件
     *
     * @param entity 实体
     * @param data buff数据
     */// First buff data init
    fun init(entity: LivingEntity, data: BuffData)

    /**
     * 如果不符合条件，是否删除Buff
     *
     * @param entity 实体
     * @param data buff数据
     * @return 是否删除Buff
     */// Whether delete the buff data or not (depends on previous)
    fun isDeleted(entity: LivingEntity, data: BuffData): Boolean

    /**
     * 测试实体与buff数据是否符合条件
     *
     * @param entity 实体
     * @param data buff数据
     * @return 是否符合条件
     */// The main function, which is used to predicate whether an entity's buff takes effect or not
    fun test(entity: LivingEntity, data: BuffData): Boolean
    override fun register() {
        BuffSystem.conditionManager.register(key, this)
    }
}