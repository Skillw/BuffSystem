package com.skillw.buffsystem.api.data


import com.skillw.asahi.api.member.context.AsahiContext
import com.skillw.buffsystem.BuffSystem.buffManager
import com.skillw.buffsystem.api.buff.Buff
import com.skillw.buffsystem.util.GsonUtils.parseToMap
import com.skillw.pouvoir.api.plugin.map.component.Keyable
import com.skillw.pouvoir.util.encodeJson
import com.skillw.pouvoir.util.livingEntity
import com.skillw.pouvoir.util.putEntity
import org.bukkit.entity.LivingEntity
import taboolib.common.platform.service.PlatformExecutor
import java.util.*
import java.util.concurrent.ConcurrentHashMap

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
    val buffKey: String,
    val entity: LivingEntity,
) : Keyable<String>, AsahiContext by AsahiContext.create(ConcurrentHashMap()) {
    val buff: Buff?
        get() = buffManager[buffKey]
    var task: PlatformExecutor.PlatformTask? = null
    fun startTask(): PlatformExecutor.PlatformTask? {
        task?.cancel()
        return buff?.startTask(this).also { task = it }
    }

    init {
        putEntity(entity)
    }

    fun cancelTask() {
        buff?.onRelease(this, entity)
        task?.cancel()
        task = null
    }

    companion object {
        @JvmStatic
        fun deserialize(key: String, uuid: UUID, string: String): BuffData? {
            val json = string.parseToMap()
            val data = BuffData(
                key,
                json["buffKey"].toString().let { if (buffManager.containsKey(it)) it else null } ?: return null,
                uuid.livingEntity() ?: return null
            )
            json.forEach {
                data[it.key] = it.value
            }
            return data
        }
    }

    fun serialize(): String {
        if (get("save").toString() == "false") {
            return ""
        }
        return encodeJson()
    }

    override fun toString(): String {
        return "BuffData(key='$key', buffKey='$buffKey', entity=$entity, task=$task)"
    }

}
