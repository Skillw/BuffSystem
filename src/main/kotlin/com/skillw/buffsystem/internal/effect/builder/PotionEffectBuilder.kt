package com.skillw.buffsystem.internal.effect.builder

import com.skillw.buffsystem.api.effect.BaseEffect
import com.skillw.buffsystem.api.effect.EffectBuilder
import com.skillw.buffsystem.internal.effect.PotionEffect
import com.skillw.pouvoir.api.annotation.AutoRegister

@AutoRegister
object PotionEffectBuilder : EffectBuilder("potion") {
    override fun build(key: String, map: Map<String, Any>): BaseEffect? {
        try {
            val potions = map["potions"] as? List<String>? ?: return null
            val ambient = map.getOrDefault("ambient", true).toString().toBoolean()
            val particles = map.getOrDefault("particles", true).toString().toBoolean()
            val icon = map.getOrDefault("icon", true).toString().toBoolean()
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