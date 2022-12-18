package com.skillw.buffsystem.api.manager

import com.skillw.buffsystem.api.effect.BaseEffect
import com.skillw.buffsystem.api.effect.EffectBuilder
import com.skillw.pouvoir.api.manager.Manager
import com.skillw.pouvoir.api.map.LowerKeyMap

/**
 * Effect builder manager
 *
 * @constructor Create empty Effect builder manager
 */
abstract class EffectBuilderManager : LowerKeyMap<EffectBuilder>(), Manager {
    /**
     * 构建Effect
     *
     * @param key Effect的id
     * @param map 参数
     * @return 构建好的Effect
     */
    abstract fun build(key: String, map: Map<String, Any>): BaseEffect?
}