package com.skillw.buffsystem.api.effect

import com.skillw.buffsystem.BuffSystem.effectManager
import com.skillw.buffsystem.api.data.BuffData
import com.skillw.pouvoir.api.able.Registrable
import org.bukkit.entity.LivingEntity

abstract class BaseEffect(override val key: String) : Registrable<String> {
    var config = false
    var release = false

    abstract fun realize(entity: LivingEntity, data: BuffData)
    abstract fun unrealize(entity: LivingEntity, data: BuffData)

    abstract override fun hashCode(): Int

    override fun register() {
        effectManager.register(this)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BaseEffect) return false

        if (config != other.config) return false
        if (release != other.release) return false

        return true
    }
}