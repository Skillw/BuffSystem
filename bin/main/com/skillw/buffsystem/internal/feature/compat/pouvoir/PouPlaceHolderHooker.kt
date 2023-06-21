package com.skillw.buffsystem.internal.feature.compat.pouvoir

import com.skillw.buffsystem.BuffSystem
import com.skillw.pouvoir.api.feature.placeholder.PouPlaceHolder
import com.skillw.pouvoir.api.plugin.annotation.AutoRegister
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
                    buff ?: return@run "NULL"
                    if (strings.size >= 3) {
                        if (strings[2] == "name") buff!!.display.colored()
                        else "NULL"
                    } else {
                        buff!!.description(this, entity)
                    }
                }
            }
        }
        return "NULL"
    }
}