package com.skillw.buffsystem.internal.manager

import com.skillw.buffsystem.BuffSystem
import com.skillw.buffsystem.api.buff.Buff
import com.skillw.buffsystem.api.data.BuffData
import com.skillw.buffsystem.api.data.BuffDataCompound
import com.skillw.buffsystem.api.manager.BuffDataManager
import com.skillw.buffsystem.internal.feature.compat.pouvoir.container.BuffContainer
import com.skillw.buffsystem.util.GsonUtils.parseToMap
import org.bukkit.entity.LivingEntity
import taboolib.common.platform.function.submit
import java.util.*
import java.util.function.Consumer

object BuffDataManagerImpl : BuffDataManager() {
    override val key = "BuffDataManager"
    override val priority = 3
    override val subPouvoir = BuffSystem

    @get:Synchronized
    @set:Synchronized
    internal var reloading = false
    override fun get(key: UUID): BuffDataCompound {
        return super.get(key) ?: BuffDataCompound(key).apply { put(key, this) }
    }


    override fun giveBuff(entity: LivingEntity, source: String, buff: Buff, consumer: Consumer<BuffData>?) {
        removeBuff(entity, source)
        val buffData = BuffData(source, buff.key, entity)
        consumer?.accept(buffData)
        buffData.startTask()
        this[entity.uniqueId].register(source, buffData)
    }

    override fun giveBuff(entity: LivingEntity, source: String, buff: Buff, json: String) {
        giveBuff(entity, source, buff) { data ->
            json.parseToMap().forEach {
                data[it.key] = it.value
            }
        }
    }

    override fun removeBuff(entity: LivingEntity, source: String) {
        this[entity.uniqueId].remove(source)
    }

    override fun clearBuff(entity: LivingEntity) {
        this[entity.uniqueId].clear()
    }

    override fun removeIf(entity: LivingEntity, json: String) {
        val uuid = entity.uniqueId
        val buffData = BuffSystem.buffDataManager[uuid] ?: return
        val condition = json.parseToMap()
        buffData.filterValues { data -> condition.all { (key, value) -> data[key] == value } }
            .forEach { (key, _) ->
                removeBuff(entity, key)
            }
    }

    override fun matches(entity: LivingEntity, json: String): Boolean {
        val uuid = entity.uniqueId
        val buffData = BuffSystem.buffDataManager[uuid] ?: return false
        val condition = json.parseToMap()
        return buffData.values.any { data -> condition.all { (key, value) -> data[key] == value } }
    }


    override fun onEnable() {
        onReload()
    }

    override fun onReload() {
        reloading = true
        values.forEach(BuffDataCompound::unrealize)
        submit(delay = 20) {
            values.forEach(BuffDataCompound::realize)
            reloading = false
        }
    }

    override fun onDisable() {
        forEach { (key, compound) ->
            compound.unrealize()
            BuffContainer[compound.entity?.name.toString(), "buff-data"] = compound.serialize()
            BuffSystem.buffDataManager.remove(key)
        }

    }
}