package com.skillw.buffsystem.internal.manager

import com.skillw.buffsystem.BuffSystem
import com.skillw.buffsystem.api.buff.Buff
import com.skillw.buffsystem.api.data.BuffData
import com.skillw.buffsystem.api.manager.BuffDataManager
import com.skillw.buffsystem.internal.feature.compat.pouvoir.container.BuffContainer
import com.skillw.buffsystem.util.GsonUtils.parseToMap
import com.skillw.pouvoir.util.EntityUtils.livingEntity
import org.bukkit.entity.LivingEntity
import taboolib.common.platform.function.submit
import taboolib.common.platform.service.PlatformExecutor
import java.util.function.Consumer

object BuffDataManagerImpl : BuffDataManager() {
    override val key = "BuffDataManager"
    override val priority = 1
    override val subPouvoir = BuffSystem

    private var buffTask: PlatformExecutor.PlatformTask? = null

    private fun createBuffScheduled(): PlatformExecutor.PlatformTask {
        return submit(async = true, period = BSConfig.updateTime) {
            for ((_, dataCompound) in this@BuffDataManagerImpl) {
                dataCompound.execAll()
            }
        }
    }

    private var task:
            PlatformExecutor.PlatformTask? = null

    private fun clearTask() {
        task?.cancel()
        task = submit(period = BSConfig.dataClearSchedule) {
            try {
                keys.forEach {
                    val livingEntity = it.livingEntity()
                    if (livingEntity?.isValid != true || it.livingEntity()?.isDead != false) {
                        remove(it)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    override fun giveBuff(entity: LivingEntity, source: String, buff: Buff, consumer: Consumer<BuffData>?) {
        removeBuff(entity, source)
        val buffData = BuffData(source, buff, entity)
        buffData.init(consumer)
        buffData.exec()
    }

    override fun giveBuff(entity: LivingEntity, source: String, buff: Buff, json: String) {
        giveBuff(entity, source, buff) { data ->
            json.parseToMap().forEach {
                data[it.key] = it.value
            }
        }
    }

    override fun removeBuff(entity: LivingEntity, source: String) {
        this[entity.uniqueId]?.remove(source)
    }

    override fun clearBuff(entity: LivingEntity) {
        this[entity.uniqueId]?.clear()
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
        this.values.forEach {
            it.unrealize()
        }
        buffTask?.cancel()
        buffTask = null
        submit(delay = 20) { buffTask = createBuffScheduled() }
        clearTask()
    }

    override fun onDisable() {
        forEach { (_, compound) ->
            compound.unrealize()
            BuffContainer[compound.entity?.name.toString(), "buff-data"] = compound.serialize()
            BuffSystem.buffDataManager.remove(compound.key)
        }

    }
}