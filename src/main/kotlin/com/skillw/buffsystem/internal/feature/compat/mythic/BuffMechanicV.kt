package com.skillw.buffsystem.internal.feature.compat.mythic

import com.skillw.buffsystem.BuffSystem
import io.lumine.mythic.api.adapters.AbstractEntity
import io.lumine.mythic.api.config.MythicLineConfig
import io.lumine.mythic.api.skills.ITargetedEntitySkill
import io.lumine.mythic.api.skills.SkillMetadata
import io.lumine.mythic.api.skills.SkillResult
import io.lumine.mythic.api.skills.placeholders.PlaceholderString

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

            "removeIf" -> {
                BuffSystem.buffDataManager.removeIf(uuid, json)
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


}