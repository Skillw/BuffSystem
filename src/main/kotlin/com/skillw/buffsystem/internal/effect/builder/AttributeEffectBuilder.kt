package com.skillw.buffsystem.internal.effect.builder

import com.skillw.buffsystem.api.effect.BaseEffect
import com.skillw.buffsystem.api.effect.EffectBuilder
import com.skillw.buffsystem.internal.effect.AttributeEffect
import com.skillw.pouvoir.api.annotation.AutoRegister

@AutoRegister
object AttributeEffectBuilder : EffectBuilder("attribute") {
    override fun build(key: String, map: Map<String, Any>): BaseEffect? {
        return AttributeEffect(key, map["attributes"] as? List<String>? ?: return null)
    }


}