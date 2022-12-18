package com.skillw.buffsystem.api.manager

import com.skillw.buffsystem.api.buff.Buff
import com.skillw.pouvoir.api.manager.Manager
import com.skillw.pouvoir.api.map.KeyMap

/**
 * Buff manager
 *
 * @constructor Create empty Buff manager
 */
abstract class BuffManager : KeyMap<String, Buff>(), Manager