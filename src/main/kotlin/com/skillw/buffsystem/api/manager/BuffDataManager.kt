package com.skillw.buffsystem.api.manager

import com.skillw.buffsystem.api.buff.Buff
import com.skillw.buffsystem.api.data.BuffData
import com.skillw.buffsystem.api.data.BuffDataCompound
import com.skillw.pouvoir.api.manager.Manager
import com.skillw.pouvoir.api.map.KeyMap
import org.bukkit.entity.LivingEntity
import java.util.*
import java.util.function.Consumer

abstract class BuffDataManager : KeyMap<UUID, BuffDataCompound>(), Manager {


    abstract fun giveBuff(entity: LivingEntity, key: String, buff: Buff, consumer: Consumer<BuffData>? = null)
    abstract fun giveBuff(uuid: UUID, key: String, buff: Buff, consumer: Consumer<BuffData>? = null)
    abstract fun giveBuff(uuid: UUID, key: String, buff: Buff, json: String)
    abstract fun giveBuff(entity: LivingEntity, key: String, buff: Buff, json: String)
    abstract fun clearBuff(entity: LivingEntity)
    abstract fun clearBuff(uuid: UUID)
    abstract fun removeBuff(entity: LivingEntity, key: String)
    abstract fun removeBuff(uuid: UUID, key: String)
    abstract fun removeIf(uuid: UUID, json: String)
    abstract fun removeIf(entity: LivingEntity, json: String)
}