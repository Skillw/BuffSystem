package com.skillw.buffsystem.internal.feature.compat.mythic

import com.skillw.buffsystem.BuffSystem
import com.skillw.buffsystem.api.BuffAPI.clearBuff
import com.skillw.buffsystem.api.BuffAPI.giveBuff
import com.skillw.buffsystem.api.BuffAPI.removeBuff
import com.skillw.buffsystem.api.BuffAPI.removeBuffIf
import org.bukkit.entity.LivingEntity

object BuffHandler {
    @JvmStatic
    internal fun LivingEntity.handle(
        type: String,
        key: String = "",
        buffKey: String = "",
        json: String = "",
        data: Map<String, Any> = HashMap(),
    ): Boolean {
        when (type) {
            "add" -> {
                val buff = BuffSystem.buffManager[buffKey] ?: error("No such buff called $buffKey")
                if (json.isNotEmpty())
                    giveBuff(key, buff, json)
                else
                    giveBuff(key, buff) {
                        it.putAll(data)
                    }
            }

            "remove" -> {
                removeBuff(key)
            }

            "removeIf" -> {
                removeBuffIf(json)
            }

            "clear" -> {
                clearBuff()
            }

            "matches" -> {
                return BuffSystem.buffDataManager.matches(this, json)
            }
        }
        return true
    }
}