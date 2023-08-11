package com.skillw.buffsystem.api.manager

import com.skillw.buffsystem.api.effect.BaseEffect
import com.skillw.pouvoir.api.manager.Manager
import com.skillw.pouvoir.api.plugin.map.KeyMap

/**
 * Effect manager
 *
 * @constructor Create empty Effect manager
 */
abstract class EffectManager : KeyMap<String, BaseEffect>(), Manager