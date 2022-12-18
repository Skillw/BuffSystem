package com.skillw.buffsystem.api.effect

import com.skillw.buffsystem.BuffSystem
import com.skillw.buffsystem.BuffSystem.effectManager
import com.skillw.buffsystem.api.data.BuffData
import com.skillw.pouvoir.api.able.Registrable
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.entity.LivingEntity
import java.util.*

/**
 * Base effect
 *
 * @constructor Create empty Base effect
 * @property key
 */
abstract class BaseEffect(override val key: String) : Registrable<String>, ConfigurationSerializable {
    /** 是否来自配置（如果为true会在重载时删掉） */
    var config = false

    /**
     * 在实体身上做效果
     *
     * @param entity 实体
     * @param data Buff数据
     */
    abstract fun realize(entity: LivingEntity, data: BuffData)

    /**
     * 去除实体身上的效果
     *
     * @param entity 实体
     * @param data Buff数据
     */
    abstract fun unrealize(entity: LivingEntity, data: BuffData)

    abstract override fun hashCode(): Int

    override fun register() {
        effectManager.register(this)
    }

    companion object {
        @JvmStatic
        fun loadEffects(map: List<Any>): List<BaseEffect> {
            val effects = LinkedList<BaseEffect>()
            map.forEach {
                when (it) {
                    is String -> effectManager[it]

                    is Map<*, *> -> BuffSystem.effectBuilderManager.build(
                        UUID.randomUUID().toString(),
                        it as? Map<String, Any>? ?: return@forEach
                    )?.apply { register() }

                    else -> null
                }?.let { effect -> effects.add(effect) } ?: return@forEach
            }
            return effects
        }

    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BaseEffect) return false

        if (config != other.config) return false

        return true
    }
}