package com.skillw.buffsystem.internal.feature.compat.mythic.common

import com.skillw.buffsystem.internal.feature.compat.mythic.BuffHandler.handle
import io.lumine.mythic.api.adapters.AbstractEntity
import io.lumine.mythic.api.config.MythicLineConfig
import io.lumine.mythic.api.skills.ITargetedEntitySkill
import io.lumine.mythic.api.skills.SkillMetadata
import io.lumine.mythic.api.skills.SkillResult
import io.lumine.mythic.api.skills.placeholders.PlaceholderString
import org.bukkit.entity.LivingEntity

class BuffMechanicV(mlc: MythicLineConfig) : ITargetedEntitySkill {

    private val keyPlaceholder: PlaceholderString
    private val buffKeyPlaceholder: PlaceholderString
    private val typePlaceholder: PlaceholderString
    private val jsonPlaceholder: PlaceholderString

    init {
        keyPlaceholder = PlaceholderString.of(mlc.getString(arrayOf("key", "k")) ?: "")
        buffKeyPlaceholder = PlaceholderString.of(mlc.getString(arrayOf("buff", "b")) ?: "")
        typePlaceholder = PlaceholderString.of(mlc.getString(arrayOf("type", "t")) ?: "")
        jsonPlaceholder = PlaceholderString.of(mlc.getString(arrayOf("data", "d")) ?: "{}")
    }


    override fun castAtEntity(data: SkillMetadata, target: AbstractEntity): SkillResult {
        val entity =
            target.bukkitEntity as? LivingEntity ?: error("The target of Mechanic 'buff' should be LivingEntity!")
        val key = keyPlaceholder.get(data, target)
        val type = typePlaceholder.get(data, target)
        val buffKey = buffKeyPlaceholder.get(data, target)
        val json = jsonPlaceholder.get(data, target)
        entity.handle(type, key, buffKey, json)
        return SkillResult.SUCCESS
    }


}