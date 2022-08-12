package com.skillw.buffsystem.internal.clock

import com.skillw.pouvoir.Pouvoir
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.function.submit
import taboolib.common5.Coerce

object Clock {
    var currentTick: Long = 0
        @Synchronized
        get
        private set

    @Awake(LifeCycle.ACTIVE)
    fun start() {
        currentTick = Coerce.toLong(Pouvoir.containerManager["BUFF", "CLOCK_TICKS"])
        submit(async = true, period = 1) {
            currentTick++
        }
    }

    @Awake(LifeCycle.DISABLE)
    fun disable() {
        Pouvoir.containerManager["BUFF", "CLOCK_TICKS"] = currentTick
    }
}