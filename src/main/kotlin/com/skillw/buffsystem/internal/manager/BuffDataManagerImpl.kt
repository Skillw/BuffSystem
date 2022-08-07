package com.skillw.buffsystem.internal.manager

import com.google.gson.Gson
import com.skillw.buffsystem.BuffSystem
import com.skillw.buffsystem.api.buff.Buff
import com.skillw.buffsystem.api.data.BuffData
import com.skillw.buffsystem.api.manager.BuffDataManager
import com.skillw.pouvoir.util.EntityUtils.livingEntity
import org.bukkit.entity.LivingEntity
import taboolib.common.platform.function.submit
import taboolib.common.platform.service.PlatformExecutor
import java.util.*
import java.util.function.Consumer

object BuffDataManagerImpl : BuffDataManager() {
    override val key = "BuffDataManager"
    override val priority = 1
    override val subPouvoir = BuffSystem

    private var buffTask: PlatformExecutor.PlatformTask? = null

    private fun createBuffScheduled(): PlatformExecutor.PlatformTask {
        return submit(period = BSConfig.updateTime) {
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


    override fun giveBuff(entity: LivingEntity,key:String, buff: Buff, consumer: Consumer<BuffData>?) {
        giveBuff(entity.uniqueId,key, buff, consumer)
    }

    override fun giveBuff(uuid: UUID,key:String, buff: Buff, consumer: Consumer<BuffData>?) {
        val buffData = BuffData(key,buff, uuid)
        buffData.init(consumer)
        buffData.exec()
    }

    override fun giveBuff(uuid: UUID,key:String, buff: Buff, json: String) {
        removeBuff(uuid, key)
        giveBuff(uuid,key,buff) { data ->
            Gson().fromJson(json, Map::class.java).forEach {
                data[it.key.toString()] = it.value ?: return@forEach
            }

        }
    }

    override fun giveBuff(entity: LivingEntity,key:String, buff: Buff, json: String) {
        giveBuff(entity.uniqueId,key, buff, json)
    }

    override fun removeBuff(entity: LivingEntity,key:String) {
        removeBuff(entity.uniqueId, key)
    }

    override fun clearBuff(entity: LivingEntity) {
        clearBuff(entity.uniqueId)
    }

    override fun removeBuff(uuid: UUID,key:String) {
        this[uuid]?.remove(key)
    }

    override fun clearBuff(uuid: UUID) {
        this[uuid]?.clear()
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
        }
    }
}