package com.skillw.buffsystem.api.data

import com.skillw.buffsystem.BuffSystem
import com.skillw.buffsystem.util.GsonUtils.parseToMap
import com.skillw.pouvoir.api.plugin.map.KeyMap
import com.skillw.pouvoir.api.plugin.map.component.Registrable
import com.skillw.pouvoir.util.livingEntity
import java.util.*

class BuffDataCompound(
    override val key: UUID,
) : Registrable<UUID>, KeyMap<String, BuffData>() {
    val entity by lazy { key.livingEntity() }

    override fun register(key: String, value: BuffData) {
        value.startTask()
        super.register(key, value)
    }

    override fun clear() {
        values.forEach(BuffData::cancelTask)
        super.clear()
    }

    override fun remove(key: String): BuffData? {
        get(key)?.cancelTask()
        return super.remove(key)
    }

    override fun register() {
        BuffSystem.buffDataManager.register(this)
    }

    companion object {
        @JvmStatic
        fun deserialize(uuid: UUID, string: String): BuffDataCompound {
            val compound = BuffDataCompound(uuid)
            if (string == "null") return compound
            string.parseToMap().forEach {
                compound[it.key] =
                    BuffData.deserialize(it.key, uuid, it.value.toString()) ?: return@forEach
            }
            return compound
        }
    }

    fun serialize(): String {
        val builder = StringBuilder("{")
        keys.forEachIndexed { index, key ->
            val data = this[key]
            val serial = data?.serialize()
            if (serial == "") return@forEachIndexed
            builder.append("$key:${data?.serialize()}")
            if (index != keys.size - 1) builder.append(", ")
        }
        builder.append("}")
        return builder.toString()
    }

    fun unrealize() {
        values.forEach(BuffData::cancelTask)
    }

    fun realize() {
        values.forEach(BuffData::startTask)
    }

}