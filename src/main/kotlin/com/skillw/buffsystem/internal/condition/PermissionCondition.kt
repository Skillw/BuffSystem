package com.skillw.buffsystem.internal.condition

import com.skillw.buffsystem.api.condition.BuffCondition
import com.skillw.buffsystem.api.data.BuffData
import com.skillw.buffsystem.internal.manager.BSConfig
import com.skillw.buffsystem.internal.manager.BSConfig.enablePermStatus
import com.skillw.buffsystem.internal.manager.BSConfig.permEachFormat
import com.skillw.buffsystem.internal.manager.BSConfig.permSeparator
import com.skillw.buffsystem.internal.manager.BSConfig.permStatusFormat
import com.skillw.pouvoir.api.annotation.AutoRegister
import org.bukkit.entity.LivingEntity
import taboolib.common.platform.function.warning

/**
 * @className PermissionCondition
 * @author Glom
 * @date 2022/7/18 8:37
 * Copyright  2022 user. All rights reserved.
 */
@AutoRegister
object PermissionCondition : BuffCondition {
    override val key = "permission"
    override val description: String
        get() = BSConfig.permDescription

    private fun buildStatus(entity: LivingEntity, permissions: Collection<String>): String {
        if (!enablePermStatus) return ""
        val each = StringBuilder()
        permissions.forEach {
            each.append(
                permEachFormat.replace("{permission}", it).replace("{bool}", entity.hasPermission(it).toString())
            )
            if (it != permissions.last()) each.append(permSeparator)
        }
        return permStatusFormat.replace("{each}", each.toString())
    }

    override fun status(entity: LivingEntity, data: BuffData): String {
        val permissions = data.getAs<Collection<String>>("permissions") ?: return ""
        return buildStatus(entity, permissions)
    }

    override fun init(entity: LivingEntity, data: BuffData) {
        if (!data.containsKey("permissions")) {
            warning("The Buff ${data.buffKey} taken effect now has no parma of 'permissions'!")
        }
    }

    override fun isDeleted(entity: LivingEntity, data: BuffData): Boolean {
        return false
    }

    override fun test(entity: LivingEntity, data: BuffData): Boolean {
        val permissions = data.getAs<Collection<String>>("permissions") ?: return false
        return permissions.all { entity.hasPermission(it) }
    }


}