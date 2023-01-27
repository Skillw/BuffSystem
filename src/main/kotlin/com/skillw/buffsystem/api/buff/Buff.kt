package com.skillw.buffsystem.api.buff

import com.skillw.asahi.api.AsahiManager
import com.skillw.asahi.api.member.context.AsahiContext
import com.skillw.asahi.api.member.namespace.NamespaceContainer
import com.skillw.asahi.api.member.namespace.NamespaceHolder
import com.skillw.buffsystem.BuffSystem
import com.skillw.buffsystem.api.data.BuffData
import com.skillw.buffsystem.api.event.BuffEvent
import com.skillw.pouvoir.api.plugin.map.component.Registrable
import com.skillw.pouvoir.util.toMap
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.entity.LivingEntity
import taboolib.common.platform.function.submit
import taboolib.common.platform.service.PlatformExecutor
import taboolib.module.chat.colored
import java.util.concurrent.ConcurrentHashMap

/**
 * @className Buff
 *
 * @author Glom
 * @date 2022/7/17 23:38 Copyright 2022 user. All rights reserved.
 */
abstract class Buff(override val key: String, val display: String, vararg namespaces: String) :
    Registrable<String>, ConfigurationSerializable, NamespaceHolder<Buff> {
    val initContext = AsahiContext.create(ConcurrentHashMap())
    var period = 10L
    var async = true
    var config = false
    override val namespaces = NamespaceContainer().apply {
        addNamespaces(AsahiManager.getNamespace("buff"))
    }

    protected abstract fun BuffData.genDescription(entity: LivingEntity): List<String>
    protected abstract fun BuffData.tick(entity: LivingEntity)
    protected abstract fun BuffData.release(entity: LivingEntity)

    internal fun startTask(data: BuffData): PlatformExecutor.PlatformTask {
        val entity = data.entity
        data.putAll(initContext)
        data["data"] = data
        return submit(async = async, period = period) {
            data["task"] = this
            onTick(data, entity)
        }
    }

    fun description(data: BuffData, entity: LivingEntity): List<String> {
        val pre = BuffEvent.Description.Pre(data)
        pre.call()
        val description = pre.data.genDescription(entity).toMutableList()
        val post = BuffEvent.Description.Post(data, description)
        post.call()
        return post.description.map { it.colored() }
    }

    fun onTick(data: BuffData, entity: LivingEntity) {
        val pre = BuffEvent.Tick.Pre(data)
        pre.call()
        data.tick(entity)
        val post = BuffEvent.Tick.Post(data)
        post.call()
    }

    fun onRelease(data: BuffData, entity: LivingEntity) {
        val pre = BuffEvent.Release.Pre(data)
        pre.call()
        data.release(entity)
        val post = BuffEvent.Release.Post(data)
        post.call()
    }


    override fun register() {
        BuffSystem.buffManager[key] = this
    }

    override fun serialize(): MutableMap<String, Any> {
        return linkedMapOf()
    }

    companion object {

        @JvmStatic
        fun deserialize(section: ConfigurationSection): Buff {
            return BuffBuilder(section.name, section.toMap()).build()
        }
    }
}