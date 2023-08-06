package com.skillw.buffsystem.api.data


import com.skillw.buffsystem.BuffSystem
import com.skillw.buffsystem.BuffSystem.buffManager
import com.skillw.buffsystem.api.buff.Buff
import com.skillw.buffsystem.util.GsonUtils.parseToMap
import com.skillw.pouvoir.api.PouvoirAPI.analysis
import com.skillw.pouvoir.api.able.Keyable
import com.skillw.pouvoir.api.map.BaseMap
import com.skillw.pouvoir.internal.core.function.context.SimpleContext
import com.skillw.pouvoir.util.EntityUtils.livingEntity
import com.skillw.pouvoir.util.GsonUtils.encodeJson
import org.bukkit.entity.LivingEntity
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Consumer

/**
 * Buff data
 *
 * @constructor Create empty Buff data
 * @property key
 * @property buff
 * @property entity
 */
class BuffData(
    override val key: String,
    val buff: Buff,
    val entity: LivingEntity,
) : Keyable<String>, BaseMap<String, Any>() {

    private val context = SimpleContext(ConcurrentHashMap())

    init {
        this["buffKey"] = buff.key
        buff.data.let { this.putAll(it) }
    }

    /** buff数据的UUID */
    val uniqueId = UUID.randomUUID()

    /** 实体的uuid */
    val uuid = entity.uniqueId

    companion object {
        @JvmStatic
        fun deserialize(key: String, uuid: UUID, string: String): BuffData? {
            val json = string.parseToMap()
            val data = BuffData(
                key,
                buffManager[json["buffKey"].toString()] ?: return null,
                uuid.livingEntity() ?: return null
            )
            json.forEach {
                data[it.key] = it.value
            }
            return data
        }
    }

    /**
     * Init
     *
     * @param consumer
     */
    fun init(consumer: Consumer<BuffData>? = null) {
        consumer?.accept(this)
        buff.init(entity, this)
    }

    /** Exec */
    fun exec() {
        if (!BuffSystem.buffDataManager.containsKey(uuid)) {
            val compound = BuffDataCompound(uuid)
            compound[key] = this
            BuffSystem.buffDataManager[uuid] = compound
        }
        if (!BuffSystem.buffDataManager[uuid]!!.containsKey(key))
            BuffSystem.buffDataManager[uuid]!![key] = this
        buff.takeEffect(entity, this)
    }

    /** Remove */
    fun remove() {
        if (BuffSystem.buffDataManager.containsKey(uuid)) {
            BuffSystem.buffDataManager[uuid]!!.remove(key)
        }
        unrealize()

    }

    /** Unrealize */
    fun unrealize() {
        buff.unrealize(entity, this)
    }

    /**
     * Serialize
     *
     * @return
     */
    fun serialize(): String {
        if (get("save").toString() == "false") {
            return ""
        }
        return encodeJson()
    }

    /**
     * Get as
     *
     * @param key
     * @param T
     * @return
     */
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

    /**
     * Handle
     *
     * @return
     */
    fun List<*>.handle(): List<Any> {
        return this.mapNotNull { it?.handle() }
    }

    /**
     * Handle
     *
     * @return
     */
    fun Any.handle(): Any {
        return when (this) {
            is String -> replacement().analysis(context)
            is List<*> -> handle()
            is Map<*, *> -> this.handle()
            else -> this
        }
    }

    /**
     * Handle
     *
     * @return
     */
    fun Map<*, *>.handle(): MutableMap<String, Any> {
        val map = HashMap<String, Any>()
        this.forEach { (keyObj, obj) ->
            val key = keyObj?.handle()?.toString() ?: return@forEach
            when (obj) {
                is Map<*, *> -> {
                    map[key] = obj.handle()
                }

                is List<*> -> {
                    map[key] = obj.handle()
                }

                is ByteArray -> {
                    map[key] = obj
                }

                is IntArray -> {
                    map[key] = obj
                }

                else -> {
                    map[key] = obj?.handle().toString()
                }
            }
        }
        return map
    }

    private fun replacedMap(): Map<String, Any> {
        return this.mapKeys { "{${it.key}}" }
    }

}
