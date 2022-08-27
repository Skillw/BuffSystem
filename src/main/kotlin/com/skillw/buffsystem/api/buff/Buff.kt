package com.skillw.buffsystem.api.buff

import com.skillw.buffsystem.BuffSystem
import com.skillw.buffsystem.api.condition.BuffCondition
import com.skillw.buffsystem.api.data.BuffData
import com.skillw.buffsystem.api.effect.BaseEffect
import com.skillw.pouvoir.api.able.Registrable
import com.skillw.pouvoir.api.map.LinkedKeyMap
import com.skillw.pouvoir.util.FileUtils.toMap
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.entity.LivingEntity
import java.util.*

/**
 * @className Buff
 *
 * @author Glom
 * @date 2022/7/17 23:38 Copyright 2022 user. All rights reserved.
 */
open class Buff(override val key: String, val name: String) : Registrable<String>, ConfigurationSerializable {
    val data = HashMap<String, Any>()
    var config = false

    internal val conditions = LinkedKeyMap<String, BuffCondition>()
    internal val effects: MutableCollection<BaseEffect> = Collections.synchronizedCollection(LinkedList())


    fun init(entity: LivingEntity, data: BuffData) {
        conditions.forEach {
            it.value.init(entity, data)
        }
    }

    fun isDeleted(entity: LivingEntity, data: BuffData): Boolean =
        conditions.map.toList()[0].second.isDeleted(entity, data)

    open fun test(entity: LivingEntity, data: BuffData): Boolean = conditions.map.all { it.value.test(entity, data) }

    fun takeEffect(entity: LivingEntity, data: BuffData) {
        if (test(entity, data)) {
            realize(entity, data)
        } else {
            unrealize(entity, data)
            if (isDeleted(entity, data)) {
                BuffSystem.buffDataManager.removeBuff(entity, data.key)
            }
        }
    }

    fun realize(entity: LivingEntity, data: BuffData) {
        effects.forEach {
            if (!BuffSystem.effectManager.containsKey(it.key)) {
                effects.remove(it)
                return@forEach
            }
            it.realize(entity, data)
        }
    }

    fun unrealize(entity: LivingEntity, data: BuffData) {
        effects.forEach {
            it.unrealize(entity, data)
            if (!BuffSystem.effectManager.containsKey(it.key)) {
                effects.remove(it)
                return@forEach
            }
        }
    }

    override fun register() {
        BuffSystem.buffManager.register(this)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Buff) return false

        if (key != other.key) return false
        if (conditions != other.conditions) return false
        if (effects != other.effects) return false

        return true
    }

    private fun <T> hashCodeCollection(list: Collection<T>): Int {
        var hashcode = 0
        list.forEach {
            hashcode += it.hashCode()
        }
        return hashcode
    }

    override fun hashCode(): Int {
        var result = key.hashCode()
        result = 31 * result + hashCodeMap(conditions)
        result = 31 * result + hashCodeCollection(effects)
        return result
    }

    private fun hashCodeMap(conditions: LinkedKeyMap<String, BuffCondition>): Int {
        var hashcode = 0
        conditions.values.forEach {
            hashcode += it.hashCode()
        }
        return hashcode
    }

    companion object {
        @JvmStatic
        fun loadBuffs(map: List<Any>): List<Buff> {
            val buffs = LinkedList<Buff>()
            map.forEach {
                when (it) {
                    is String -> BuffSystem.buffManager[it]

                    is Map<*, *> -> build(UUID.randomUUID().toString(), it as Map<String, Any>)

                    else -> null
                }?.let { effect -> buffs.add(effect) } ?: return@forEach
            }
            return buffs
        }

        @JvmStatic
        fun deserialize(section: ConfigurationSection): Buff? {
            return build(section.name, section.toMap())
        }

        @JvmStatic
        fun build(key: String, map: Map<String, Any>): Buff? {
            try {
                val name = map["name"].toString()
                val data = map["data"] as? Map<String, Any>? ?: emptyMap()
                val conditions =
                    (map["conditions"] as? List<String>? ?: emptyList()).mapNotNull { BuffSystem.conditionManager[it] }
                        .associateBy { it.key }
                val effects = BaseEffect.loadEffects(map["effects"] as? List<Any>? ?: emptyList())
                val buff = Buff(key, name)
                buff.data.putAll(data)
                buff.conditions.putAll(conditions)
                buff.effects.addAll(effects)
                buff.config = true
                return buff
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }
    }

    override fun serialize(): MutableMap<String, Any> {
        return linkedMapOf(
            "conditions" to conditions.map { it.key },
            "effects" to LinkedHashMap<String, Any>().apply {
                effects.forEach { put(it.key, it.serialize()) }
            })
    }

    fun status(entity: LivingEntity, data: BuffData): String {
        return StringBuilder().apply {
            conditions.forEach { (_, condition) -> append(condition.status(entity, data)) }
        }.toString()
    }

}