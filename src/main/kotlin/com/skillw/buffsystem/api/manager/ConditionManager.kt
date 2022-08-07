package com.skillw.buffsystem.api.manager

import com.skillw.buffsystem.api.condition.BuffCondition
import com.skillw.pouvoir.api.manager.Manager
import com.skillw.pouvoir.api.map.KeyMap

abstract class ConditionManager : KeyMap<String, BuffCondition>(), Manager {

    abstract fun descriptions(): List<String>
}