package com.skillw.buffsystem.internal.effect

import com.skillw.buffsystem.api.data.BuffData
import com.skillw.buffsystem.api.effect.BaseEffect
import com.skillw.buffsystem.api.event.EffectLoadEvent
import com.skillw.pouvoir.Pouvoir
import com.skillw.pouvoir.api.map.BaseMap
import com.skillw.pouvoir.util.MapUtils.put
import org.bukkit.Bukkit
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.entity.LivingEntity
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submit
import taboolib.common5.Coerce
import java.util.*

class PotionEffect(override val key: String, private val potions: List<String>) : BaseEffect(),
    ConfigurationSerializable {


    private var ambient = true
    private var particles = true
    private var icon = true

    override fun realize(entity: LivingEntity, data: BuffData) {
        val uuid = entity.uniqueId
        val potionsStr = data.replace(this.potions).map { Pouvoir.pouPlaceHolderAPI.replace(entity, it) }
        submit {
            potionMap[entity.uniqueId]?.clear()
            for (it in potionsStr) {
                val array = it.split(":")
                val potionType = PotionEffectType.getByName(array[0]) ?: continue
                val level = Coerce.toInteger(array[1])
                if (level <= 0) continue
                entity.addPotionEffect(PotionEffect(potionType, 100000000, level, ambient, particles, icon))
                potionMap.put(uuid, potionType)
            }
        }
    }

    override fun unrealize(entity: LivingEntity, data: BuffData) {
        if (!Bukkit.isStopping())
            submit {
                potionMap[entity.uniqueId]?.forEach { entity.removePotionEffect(it) }
                potionMap[entity.uniqueId]?.clear()
            }
        else {
            potionMap[entity.uniqueId]?.forEach { entity.removePotionEffect(it) }
            potionMap[entity.uniqueId]?.clear()
        }
    }

    companion object {
        @SubscribeEvent
        fun load(event: EffectLoadEvent) {
            event.result = deserialize(event.section) ?: return
        }

        private val potionMap = BaseMap<UUID, HashSet<PotionEffectType>>()

        @JvmStatic
        fun deserialize(section: ConfigurationSection): com.skillw.buffsystem.internal.effect.PotionEffect? {
            try {
                val key = section.name
                if (section["type"].toString().lowercase() != "potion") return null
                val ambient = section.getBoolean("ambient", true)
                val particles = section.getBoolean("particles", true)
                val icon = section.getBoolean("icon", true)
                val potions = section.getStringList("potions")
                return PotionEffect(key, potions).apply {
                    this.config = true
                    this.ambient = ambient
                    this.particles = particles
                    this.icon = icon
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }
    }

    override fun serialize(): MutableMap<String, Any> {
        return linkedMapOf("type" to "potion", "potions" to potions)
    }

    override fun hashCode(): Int {
        var result = key.hashCode()
        result = 31 * result + potions.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as com.skillw.buffsystem.internal.effect.PotionEffect

        if (key != other.key) return false
        if (potions != other.potions) return false

        return true
    }
}