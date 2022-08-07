package com.skillw.buffsystem.internal.hook.pou

import com.skillw.buffsystem.BuffSystem
import com.skillw.pouvoir.api.annotation.AutoRegister
import com.skillw.pouvoir.api.placeholder.PouPlaceHolder
import org.bukkit.entity.LivingEntity
import taboolib.module.chat.colored

@AutoRegister
object PouPlaceHolderHooker : PouPlaceHolder("bs", BuffSystem) {


    override fun onPlaceHolderRequest(params: String, entity: LivingEntity, def: String): String {
        val lower = params.lowercase().replace(":", "_")
        val uuid = entity.uniqueId
        val strings = if (lower.contains("_")) lower.split("_").toMutableList() else mutableListOf(lower)
        when (strings[0]) {
            "buffs" -> {
                return BuffSystem.buffDataManager[uuid]?.keys.toString()
            }
            "buff" -> {
                if (strings.size < 3) return "NULL"
                val key = strings[1]
                BuffSystem.buffDataManager[uuid]?.get(key)?.run {
                    return when(val condition = strings[2]){
                        "name" -> buff?.name?.colored().toString()
                        else-> buff?.conditions?.get(condition)?.status(entity, this)?.colored() ?: "NULL"
                    }
                }
            }
        }
        return "NULL"
    }
}