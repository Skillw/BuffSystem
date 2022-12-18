package com.skillw.buffsystem.api.manager

import com.skillw.buffsystem.api.condition.BuffCondition
import com.skillw.pouvoir.api.manager.Manager
import com.skillw.pouvoir.api.map.KeyMap

/**
 * Condition manager
 *
 * @constructor Create empty Condition manager
 */
abstract class ConditionManager : KeyMap<String, BuffCondition>(), Manager {

    /**
     * 所有条件的描述
     *
     * @return
     */
    abstract fun descriptions(): List<String>
}