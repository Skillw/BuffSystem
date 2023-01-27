package com.skillw.buffsystem.api.buff

import com.skillw.asahi.api.AsahiAPI.compile
import com.skillw.asahi.api.member.context.AsahiContext
import com.skillw.asahi.api.script.AsahiCompiledScript
import com.skillw.asahi.util.toLazyMap
import com.skillw.buffsystem.api.data.BuffData
import com.skillw.pouvoir.api.plugin.map.DataMap
import com.skillw.pouvoir.internal.feature.handler.toInvoker
import com.skillw.pouvoir.util.putEntity
import org.bukkit.entity.LivingEntity
import taboolib.common.util.asList
import taboolib.common5.cbool
import java.util.*

/**
 * @className BuffBuilder
 *
 * @author Glom
 * @date 2023/1/24 12:11 Copyright 2023 user. All rights reserved.
 */
class BuffBuilder(
    val key: String,
    context: AsahiContext = AsahiContext.create(),
    receiver: BuffBuilder.() -> Unit = {},
) : AsahiContext by context {
    /** Display */
    var display = key

    /** Config */
    var config: Boolean = false

    /** Import */
    val import = HashSet<String>()

    /** Namespaces */
    val namespaces = HashSet<String>().apply { add("buff") }

    /** Period */
    var period = 10L

    /** Async */
    var async = true

    /** Description */
    protected var description: (BuffData.(LivingEntity) -> List<String>)? = null

    /** Control */
    val control = LinkedList<Map<String, Any>>()

    init {
        this.receiver()
    }

    /**
     * Description
     *
     * @param description
     * @return
     * @receiver
     */
    fun description(description: (BuffData.(LivingEntity) -> List<String>) = { emptyList() }): BuffBuilder {
        this.description = description
        return this
    }

    constructor(key: String, map: Map<String, Any> = emptyMap()) : this(key, AsahiContext.create()) {
        val data = DataMap().apply { putAll(map) }
        period = data.get("period", 10L)
        async = data.get("async", true)
        namespaces.addAll(data.get("namespaces", listOf("common", "lang")))
        import.addAll(data.get("import", emptyList()))
        context().putAll(data.get("context", emptyMap<String, Any>()).toLazyMap())
        control.addAll(data.get("when", emptyList<Map<String, Any>>()).map {
            it.mapValues { entry ->
                if (entry.key == "if") entry.value.toString()
                    .compile(*namespaces.toTypedArray()) else entry.value
            }
        })

        if (data.containsKey("description")) {
            kotlin.run {
                val value = data.get("description")!!.toInvoker("description", namespaces) ?: return@run
                description { entity ->
                    putEntity(entity)
                    value.invoke(this)?.asList() ?: emptyList()
                }
            }
        }
        data.get("functions", emptyMap<String, Any>()).forEach { (key, value) ->
            invokers[key] = value.toInvoker(key, namespaces) ?: return@forEach
        }
        config = true
    }

    /**
     * Build
     *
     * @return
     */
    fun build(): Buff {
        val builder = this
        return object : Buff(builder.key, builder.display, *builder.namespaces.toTypedArray()) {
            val buff = this

            init {
                period = builder.period
                async = builder.async
                initContext.import(*builder.import.toTypedArray())
                initContext.putAll(builder.context())
            }

            override fun BuffData.tick(entity: LivingEntity) {
                this["data"] = this
                val goto = builder.control.firstOrNull { (it["if"] as? AsahiCompiledScript?)?.run().cbool }?.get("goto")
                    ?: builder.control.lastOrNull()?.get("else") ?: "main"
                invokers[goto]?.invoke(this)
            }

            override fun BuffData.release(entity: LivingEntity) {
                this["data"] = this
                invokers["release"]?.invoke(this)
            }

            override fun BuffData.genDescription(entity: LivingEntity): List<String> {
                return builder.description?.invoke(this, entity) ?: emptyList()
            }
        }
    }

}