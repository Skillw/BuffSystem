package com.skillw.buffsystem.internal.feature.compat.mythic

import com.skillw.buffsystem.BuffSystem
import io.lumine.xikage.mythicmobs.adapters.AbstractEntity
import io.lumine.xikage.mythicmobs.io.MythicLineConfig
import io.lumine.xikage.mythicmobs.skills.ITargetedEntitySkill
import io.lumine.xikage.mythicmobs.skills.SkillMechanic
import io.lumine.xikage.mythicmobs.skills.SkillMetadata
import io.lumine.xikage.mythicmobs.skills.placeholders.parsers.PlaceholderString


class BuffMechanicIV(skill: String?, mlc: MythicLineConfig) : SkillMechanic(skill, mlc), ITargetedEntitySkill {

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


    override fun castAtEntity(data: SkillMetadata, target: AbstractEntity): Boolean {

        val uuid = target.bukkitEntity.uniqueId
        when (type) {
            "add" -> {
                val buff = BuffSystem.buffManager[buffKey] ?: return false
                BuffSystem.buffDataManager.giveBuff(
                    uuid,
                    key,
                    buff,
                    json.replace("<caster.level>", data.caster.level.toString())
                )
                return true
            }

            "remove" -> {
                BuffSystem.buffDataManager.removeBuff(uuid, key)
                return true
            }

            "removeIf" -> {
                BuffSystem.buffDataManager.removeIf(uuid, json)
                return true
            }

            "clear" -> {
                BuffSystem.buffDataManager.clearBuff(uuid)
                return true
            }

            else -> {
                return false
            }
        }
    }


}