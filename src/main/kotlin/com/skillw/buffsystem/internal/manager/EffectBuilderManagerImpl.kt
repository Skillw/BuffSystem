package com.skillw.buffsystem.internal.manager

import com.skillw.buffsystem.BuffSystem
import com.skillw.buffsystem.api.effect.BaseEffect
import com.skillw.buffsystem.api.manager.EffectBuilderManager

object EffectBuilderManagerImpl : EffectBuilderManager() {
    override val key = "EffectBuilderManager"
    override val priority = 2
    override val subPouvoir = BuffSystem

    override fun build(key: String, map: Map<String, Any>): BaseEffect? {
        forEach { (type, builder) ->
            if (map["type"].toString().lowercase() != type) return@forEach
            return builder.build(key, map) ?: return@forEach
        }
        return null
    }


}