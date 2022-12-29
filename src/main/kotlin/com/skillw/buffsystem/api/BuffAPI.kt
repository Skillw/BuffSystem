package com.skillw.buffsystem.api

import com.skillw.buffsystem.BuffSystem.buffDataManager
import com.skillw.buffsystem.api.buff.Buff
import com.skillw.buffsystem.api.data.BuffData

import org.bukkit.entity.LivingEntity
import java.util.function.Consumer

object BuffAPI {

    /**
     * 有无此Buff源
     *
     * @param source String buff源
     * @return Boolean
     * @receiver LivingEntity
     */
    @JvmStatic
    fun LivingEntity.hasBuff(source: String): Boolean {
        return buffDataManager[uniqueId]?.containsKey(source) == true
    }

    /**
     * 给buff
     *
     * @param source String Buff源
     * @param buff Buff Buff
     * @param consumer Consumer<BuffData>? 处理Buff数据
     * @receiver LivingEntity
     */
    @JvmStatic
    fun LivingEntity.giveBuff(source: String, buff: Buff, consumer: Consumer<BuffData>? = null) {
        buffDataManager.giveBuff(this, source, buff, consumer)
    }

    /**
     * 给buff
     *
     * @param source String Buff源
     * @param buff Buff Buff
     * @param json String buff数据(json格式)
     * @receiver LivingEntity
     */
    @JvmStatic
    fun LivingEntity.giveBuff(source: String, buff: Buff, json: String) {
        buffDataManager.giveBuff(this, source, buff, json)
    }

    /**
     * 清除实体身上所有buff
     *
     * @receiver LivingEntity
     */
    @JvmStatic
    fun LivingEntity.clearBuff() {
        buffDataManager.clearBuff(this)
    }

    /**
     * 删除实体身上Buff源
     *
     * @param source String Buff源
     * @receiver LivingEntity
     */
    @JvmStatic
    fun LivingEntity.removeBuff(source: String) {
        buffDataManager.removeBuff(this, source)
    }


    /**
     * 检查实体身上是否有符合json的buff数据
     *
     * @param json String
     * @receiver LivingEntity
     */
    @JvmStatic
    fun LivingEntity.removeBuffIf(json: String) {
        buffDataManager.removeIf(this, json)
    }
}