package com.skillw.buffsystem.api.data


import com.skillw.buffsystem.BuffSystem
import com.skillw.buffsystem.api.buff.Buff
import com.skillw.buffsystem.util.GsonUtils.parseToMap
import com.skillw.pouvoir.api.PouvoirAPI.analysis
import com.skillw.pouvoir.api.PouvoirAPI.placeholder
import com.skillw.pouvoir.api.able.Keyable
import com.skillw.pouvoir.api.map.BaseMap
import com.skillw.pouvoir.internal.function.context.SimpleContext
import com.skillw.pouvoir.util.EntityUtils.livingEntity
import com.skillw.pouvoir.util.GsonUtils.encodeJson
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Consumer

class BuffData(
    override val key: String,
    val buffKey: String,
    val uuid: UUID,
) : Keyable<String>, BaseMap<String, Any>() {

    private val context: SimpleContext = SimpleContext(ConcurrentHashMap())

    constructor(key: String, buff: Buff, uuid: UUID) : this(key, buff.key, uuid)

    val entity = uuid.livingEntity()

    init {
        this["buffKey"] = buffKey
        buff?.data?.let { this.putAll(it) }
    }

    val buff: Buff?
        get() = BuffSystem.buffManager[buffKey]

    val uniqueId = UUID.randomUUID()

    companion object {
        @JvmStatic
        fun deserialize(key: String, uuid: UUID, string: String): BuffData {
            val json = string.parseToMap()
            val data = BuffData(key, json["buffKey"].toString(), uuid)
            json.forEach {
                data[it.key] = it.value
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
        if (get("save").toString() == "false") {
            return ""
        }
        return map.encodeJson()
    }

    fun <T> getAs(key: String): T? {
        return get(key) as? T?
    }

    private fun String.replacement(): String {
        var temp = this
        replacedMap().forEach {
            temp = temp.replace(it.key, it.value.toString())
        }
        return temp
    }

    fun handle(string: String): String {
        val value = string.replacement().placeholder(entity ?: return string, false)
        return value.analysis(context)
    }

    fun <T> handle(replaces: Collection<T>): List<String> {
        return replaces.map { handle(it.toString()) }
    }

    fun handleMap(map: Map<*, *>): Map<String, Any> {
        return map.mapKeys { it.key.toString() }.mapValues { handle(it.value!!) }
    }

    fun handle(any: Any): Any {
        return when (any) {
            is String -> {
                handle(any)
            }

            is Collection<*> -> {
                handle(any)
            }

            is Map<*, *> -> {
                handleMap(any)
            }

            else -> any
        }
    }

    private fun replacedMap(): Map<String, Any> {
        return this.mapKeys { "{${it.key}}" }
    }

}
