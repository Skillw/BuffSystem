package com.skillw.buffsystem.api.event

import com.skillw.buffsystem.api.effect.BaseEffect
import org.bukkit.configuration.ConfigurationSection
import taboolib.platform.type.BukkitProxyEvent

/**
 * @className EffectLoadEvent
 * @author Glom
 * @date 2022/7/17 20:51
 * Copyright  2022 user. All rights reserved.
 */
class EffectLoadEvent(val section: ConfigurationSection, var result: BaseEffect? = null) : BukkitProxyEvent()