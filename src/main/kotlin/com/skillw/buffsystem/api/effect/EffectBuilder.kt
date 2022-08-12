package com.skillw.buffsystem.api.effect

import com.skillw.buffsystem.BuffSystem
import com.skillw.pouvoir.api.able.Registrable

/**
 * @className EffectBuilder
 *
 * @author Glom
 * @date 2022/8/11 14:29 Copyright 2022 user. All rights reserved.
 */
abstract class EffectBuilder(override val key: String) : Registrable<String> {
    abstract fun build(key: String, map: Map<String, Any>): BaseEffect?
    override fun register() {
        BuffSystem.effectBuilderManager.register(this)
    }
}