package com.skillw.buffsystem.api.manager

import com.skillw.buffsystem.api.buff.Buff
import com.skillw.buffsystem.api.data.BuffData
import com.skillw.buffsystem.api.data.BuffDataCompound
import com.skillw.pouvoir.api.manager.Manager
import com.skillw.pouvoir.api.map.KeyMap
import org.bukkit.entity.LivingEntity
import java.util.*
import java.util.function.Consumer

/**
 * Buff data manager
 *
 * @constructor Create empty Buff data manager
 */
abstract class BuffDataManager : KeyMap<UUID, BuffDataCompound>(), Manager {
    /**
     * 给Buff
     *
     * @param entity 实体
     * @param source Buff源
     * @param buff buff
     * @param consumer 处理buff数据
     */
    abstract fun giveBuff(entity: LivingEntity, source: String, buff: Buff, consumer: Consumer<BuffData>? = null)

    /**
     * 给Buff
     *
     * @param entity 实体
     * @param source Buff源
     * @param buff buff
     * @param json json
     */
    abstract fun giveBuff(entity: LivingEntity, source: String, buff: Buff, json: String)

    /**
     * 清buff
     *
     * @param entity 实体
     */
    abstract fun clearBuff(entity: LivingEntity)

    /**
     * 删buff
     *
     * @param entity 实体
     * @param source buff源
     */
    abstract fun removeBuff(entity: LivingEntity, source: String)

    /**
     * 删除符合条件的buff数据
     *
     * @param entity 实体
     * @param json json
     */
    abstract fun removeIf(entity: LivingEntity, json: String)

    /**
     * 是否有符合条件(符合json)的buff数据
     *
     * @param entity 实体
     * @param json json
     * @return 是否有符合条件(符合json)的buff数据
     */
    abstract fun matches(entity: LivingEntity, json: String): Boolean
}