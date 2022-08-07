package com.skillw.buffsystem.internal.clock

import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.function.submit

object Clock {
    var currentTick: Long = 0
        get() {
            synchronized(field) {
                return field
            }
        }
        private set

    @Awake(LifeCycle.ACTIVE)
    fun start() {
        submit(async = true, period = 1) {
            currentTick++
        }
    }
}