package com.skillw.buffsystem.api.manager

import com.skillw.buffsystem.api.effect.BaseEffect
import com.skillw.buffsystem.api.effect.EffectBuilder
import com.skillw.pouvoir.api.manager.Manager
import com.skillw.pouvoir.api.map.LowerKeyMap

abstract class EffectBuilderManager : LowerKeyMap<EffectBuilder>(), Manager {
    abstract fun build(key: String, map: Map<String, Any>): BaseEffect?
}