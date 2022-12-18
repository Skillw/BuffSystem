package com.skillw.buffsystem.internal.core.effect.builder

import com.skillw.buffsystem.api.effect.BaseEffect
import com.skillw.buffsystem.api.effect.EffectBuilder
import com.skillw.buffsystem.internal.core.effect.PermissionEffect
import com.skillw.pouvoir.api.annotation.AutoRegister

@AutoRegister
object PermissionEffectBuilder : EffectBuilder("permission") {
    override fun build(key: String, map: Map<String, Any>): BaseEffect? {
        try {
            val permissions = map["permissions"] as? List<String>? ?: return null
            return PermissionEffect(key, permissions).apply { config = true }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }


}