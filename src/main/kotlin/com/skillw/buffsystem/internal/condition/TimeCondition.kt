package com.skillw.buffsystem.internal.condition

import com.skillw.buffsystem.BuffSystem
import com.skillw.buffsystem.api.condition.BuffCondition
import com.skillw.buffsystem.api.data.BuffData
import com.skillw.buffsystem.internal.clock.Clock.currentTick
import com.skillw.buffsystem.internal.manager.BSConfig
import com.skillw.buffsystem.internal.manager.BSConfig.enableTimeStatus
import com.skillw.buffsystem.internal.manager.BSConfig.second
import com.skillw.buffsystem.internal.manager.BSConfig.timeMode
import com.skillw.buffsystem.internal.manager.BSConfig.timeStatusFormat
import com.skillw.buffsystem.internal.manager.BSConfig.unlimited
import com.skillw.pouvoir.api.annotation.AutoRegister
import com.skillw.pouvoir.util.NumberUtils.format
import org.bukkit.entity.LivingEntity
import taboolib.common.platform.function.warning
import kotlin.math.max

/**
 * @className TimeCondition
 *
 * @author Glom
 * @date 2022/7/18 7:39 Copyright 2022 user. All rights reserved.
 */
@AutoRegister
object TimeCondition : BuffCondition {
    override val key = "time"
    override val description: String
        get() = BuffSystem.config["conditions.time.description"].toString()


    private fun format(tick: Long): String {
        if (tick < 0) return unlimited
        return if (timeMode == "second") ((tick.toDouble() / 20.0) + 0.05).format() + second else (tick + 1).toString() + BSConfig.tick
    }

    override fun status(entity: LivingEntity, data: BuffData): String {
        return if (enableTimeStatus) timeStatusFormat.replace("{remainder}", format(remainder(data))) else ""
    }


    override fun init(entity: LivingEntity, data: BuffData) {
        if (!data.containsKey("duration")) {
            warning("The Buff ${data.buffKey} taken effect now has no parma of 'duration'!")
        }
        val duration = data.getAs<Number>("duration")?.toInt() ?: 0

        data["start"] = currentTick.toInt()
        data["end"] = (if (duration != -1) currentTick + duration else -1).toInt()
    }

    override fun isDeleted(entity: LivingEntity, data: BuffData): Boolean = isDated(data)

    fun isDated(data: BuffData): Boolean {
        val end = data.getAs<Number>("end")?.toInt() ?: return false
        if (end == -1) return false
        return currentTick >= end
    }

    fun remainder(data: BuffData): Long {
        val end = data.getAs<Number>("end")?.toInt() ?: return 0
        if (end == -1) return -1
        val remainder = end - currentTick
        return max(remainder, 0L)
    }

    override fun test(entity: LivingEntity, data: BuffData): Boolean = !isDated(data)
}