package com.skillw.buffsystem.api

import com.skillw.buffsystem.BuffSystem.buffDataManager
import com.skillw.buffsystem.api.buff.Buff
import com.skillw.buffsystem.api.data.BuffData
import org.bukkit.entity.LivingEntity
import java.util.function.Consumer

object BuffAPI {

    @JvmStatic
    fun LivingEntity.hasBuff(key:String) :Boolean {
       return buffDataManager[uniqueId]?.containsKey(key) == true
    }



    @JvmStatic
    fun LivingEntity.giveBuff(key: String, buff: Buff, consumer: Consumer<BuffData>? = null) {
        buffDataManager.giveBuff(this, key, buff, consumer)
    }

    @JvmStatic
    fun LivingEntity.giveBuff(key: String, buff: Buff, json: String) {
        buffDataManager.giveBuff(this, key, buff, json)
    }


    @JvmStatic
    fun LivingEntity.clearBuff() {
        buffDataManager.clearBuff(this)
    }

    @JvmStatic
    fun LivingEntity.removeBuff(key: String) {
        buffDataManager.removeBuff(this, key)
    }


    @JvmStatic
    fun LivingEntity.removeIf(key: String, json: String) {
        buffDataManager.removeIf(this, json)
    }
}