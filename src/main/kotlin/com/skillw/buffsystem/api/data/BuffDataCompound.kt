package com.skillw.buffsystem.api.data

import com.google.gson.Gson
import com.skillw.buffsystem.BuffSystem
import com.skillw.pouvoir.api.able.Registrable
import com.skillw.pouvoir.api.map.KeyMap
import com.skillw.pouvoir.util.EntityUtils.livingEntity
import java.util.*

class BuffDataCompound(
    override val key: UUID
) : Registrable<UUID>, KeyMap<String, BuffData>() {

    val entity
        get() = key.livingEntity()

    fun execAll() {
        forEach {
            if (!BuffSystem.buffManager.containsKey(it.value.buffKey)) {
                this.remove(it.key)
                return@forEach
            }
            it.value.exec()
        }
    }

    companion object {
        @JvmStatic
        fun deserialize(uuid: UUID, string: String): BuffDataCompound {
            val compound = BuffDataCompound(uuid)
            if (string == "null") return compound
            val json = Gson().fromJson(string, Map::class.java)
            json.forEach {
                compound[it.key.toString()] =
                    BuffData.deserialize(it.key.toString(), uuid, it.value?.toString() ?: return@forEach)
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

    override fun register() {
        BuffSystem.buffDataManager[key] = this
    }

    override fun remove(key: String): BuffData? {
        return super.remove(key)?.also {
            it.buff?.unrealize(entity ?: return null, it)
        }
    }

    override fun clear() {
        this.keys.forEach { remove(it) }
    }

    fun unrealize() {
        this.values.forEach {
            it.unrealize()
        }
    }
}