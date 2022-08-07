package com.skillw.buffsystem.internal.hook.mythic

import com.skillw.buffsystem.BuffSystem
import io.lumine.mythic.api.adapters.AbstractEntity
import io.lumine.mythic.api.config.MythicLineConfig
import io.lumine.mythic.api.skills.ITargetedEntitySkill
import io.lumine.mythic.api.skills.SkillMetadata
import io.lumine.mythic.api.skills.SkillResult
import io.lumine.mythic.api.skills.placeholders.PlaceholderString
import io.lumine.mythic.bukkit.events.MythicMechanicLoadEvent
import taboolib.common.platform.event.OptionalEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common5.Coerce

class BuffMechanicV(mlc: MythicLineConfig) : ITargetedEntitySkill {

    private val key: String
    private val buffKey: String
    private val type: String
    private val json: String

    init {
        key = PlaceholderString.of(mlc.getString(arrayOf("key", "k")) ?: "").get()
        buffKey = PlaceholderString.of(mlc.getString(arrayOf("buff", "b")) ?: "").get()
        type = PlaceholderString.of(mlc.getString(arrayOf("type", "t")) ?: "").get()
        json = PlaceholderString.of(mlc.getString(arrayOf("data", "d")) ?: "{}").get()
    }


    override fun castAtEntity(data: SkillMetadata, target: AbstractEntity): SkillResult {
        val uuid = target.uniqueId
        when (type) {
            "add" -> {
                val buff = BuffSystem.buffManager[buffKey] ?: return SkillResult.INVALID_CONFIG
                BuffSystem.buffDataManager.giveBuff(
                    uuid,
                    key,
                    buff,
                    json.replace("<caster.level>", data.caster.level.toString())
                )
                return SkillResult.SUCCESS
            }

            "remove" -> {
                BuffSystem.buffDataManager.removeBuff(uuid, key)
                return SkillResult.SUCCESS
            }

            "clear" -> {
                BuffSystem.buffDataManager.clearBuff(uuid)
                return SkillResult.SUCCESS
            }

            else -> {
                return SkillResult.SUCCESS
            }
        }
    }

    object MMVListener {
        @SubscribeEvent(bind = "io.lumine.mythic.bukkit.events.MythicMechanicLoadEvent")
        fun onMythicMechanicLoad(optionalEvent: OptionalEvent) {
            val event = optionalEvent.get<MythicMechanicLoadEvent>()
            if (event.mechanicName.lowercase() == "buff-sys") {
                event.register(BuffMechanicV(event.config))
            }
        }
    }
}