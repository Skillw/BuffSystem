package com.skillw.buffsystem.api.data


import com.google.gson.Gson
import com.skillw.buffsystem.BuffSystem
import com.skillw.buffsystem.api.buff.Buff
import com.skillw.pouvoir.api.able.Keyable
import com.skillw.pouvoir.api.map.BaseMap
import com.skillw.pouvoir.util.EntityUtils.livingEntity
import com.skillw.pouvoir.util.GsonUtils.encodeJson
import com.skillw.pouvoir.util.StringUtils.replacement
import java.util.*
import java.util.function.Consumer

class BuffData(
    override val key: String,
    val buffKey: String,
    val uuid: UUID
) : Keyable<String>, BaseMap<String, Any>() {

    constructor(key: String,buff: Buff, uuid: UUID) : this(key,buff.key, uuid)

    val entity
        get() = uuid.livingEntity()

    val buff: Buff?
        get() = BuffSystem.buffManager[buffKey] ?: kotlin.run { remove(); null }

    companion object {
        @JvmStatic
        fun deserialize(buffKey: String, uuid: UUID, string: String): BuffData {
            val json = Gson().fromJson(string, Map::class.java)
            val data = BuffData(json["key"].toString(),buffKey, uuid)
            json.forEach {
                data[it.key.toString()] = it.value ?: return@forEach
            }
            return data
        }
    }

    fun init(consumer: Consumer<BuffData>? = null) {
        consumer?.accept(this)
        entity?.let {
            buff?.init(it, this)
        }
    }

    fun exec() {
        val entity = entity ?: return
        if (!BuffSystem.buffDataManager.containsKey(uuid)) {
            val compound = BuffDataCompound(uuid)
            compound[key] = this
            BuffSystem.buffDataManager[uuid] = compound
        }
        if (!BuffSystem.buffDataManager[uuid]!!.containsKey(key))
            BuffSystem.buffDataManager[uuid]!![key] = this
        buff?.takeEffect(entity, this)
    }

    fun remove() {
        if (BuffSystem.buffDataManager.containsKey(uuid)) {
            BuffSystem.buffDataManager[uuid]!!.remove(key)
        }
        unrealize()
    }

    fun unrealize() {
        buff?.unrealize(entity ?: return, this)
    }

    fun serialize(): String {
        if (get("save")?.equals(false) == true) {
            return ""
        }

        return this.encodeJson()
    }

    fun <T> getAs(key: String): T? {
        return super.get(key) as? T?
    }

    fun replace(string: String): String {
        val replaced = string.replacement(replacedMap())
        return replaced
    }

    fun <T> replace(replaces: Collection<T>): List<String> {
        return replaces.map { replace(it.toString()) }
    }

    private fun replacedMap(): Map<String, Any> {
        return this.mapKeys { "{${it.key}}" }
    }

}
