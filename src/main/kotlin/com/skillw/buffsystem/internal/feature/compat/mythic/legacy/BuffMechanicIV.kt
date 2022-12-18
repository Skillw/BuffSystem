package com.skillw.buffsystem.internal.feature.compat.mythic.legacy

import com.skillw.buffsystem.internal.feature.compat.mythic.BuffHandler.handle
import io.lumine.xikage.mythicmobs.adapters.AbstractEntity
import io.lumine.xikage.mythicmobs.io.MythicLineConfig
import io.lumine.xikage.mythicmobs.skills.ITargetedEntitySkill
import io.lumine.xikage.mythicmobs.skills.SkillMechanic
import io.lumine.xikage.mythicmobs.skills.SkillMetadata
import io.lumine.xikage.mythicmobs.skills.placeholders.parsers.PlaceholderString
import org.bukkit.entity.LivingEntity


class BuffMechanicIV(skill: String?, mlc: MythicLineConfig) : SkillMechanic(skill, mlc), ITargetedEntitySkill {

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


    override fun castAtEntity(data: SkillMetadata, target: AbstractEntity): Boolean {
        val entity =
            target.bukkitEntity as? LivingEntity ?: error("The target of Mechanic 'buff' should be LivingEntity!")
        val key = keyPlaceholder.get(data, target)
        val type = typePlaceholder.get(data, target)
        val buffKey = buffKeyPlaceholder.get(data, target)
        val json = jsonPlaceholder.get(data, target)
        entity.handle(type, key, buffKey, json)
        return true
    }


}