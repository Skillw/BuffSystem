package com.skillw.buffsystem.internal.effect

import com.skillw.buffsystem.api.data.BuffData
import com.skillw.buffsystem.api.effect.BaseEffect
import com.skillw.pouvoir.api.map.BaseMap
import com.skillw.pouvoir.util.MapUtils.put
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import taboolib.common.platform.function.submit
import taboolib.common5.Coerce
import taboolib.platform.compat.VaultService
import java.util.*

class PermissionEffect(key: String, val permissions: List<String>) : BaseEffect(key),
    ConfigurationSerializable {

    init {
        release = true
    }

    override fun realize(entity: LivingEntity, data: BuffData) {
        if (entity !is Player) return
        val player = entity
        val uuid = player.uniqueId
        val permissionStr = data.run { permissions.handle().map { it.toString() } }
        permissionMap[uuid]?.clear()
        unrealize(entity, data)
        for (it in permissionStr) {
            val array = it.split(":")
            val permission = array[0]
            val has = Coerce.toBoolean(array[1])
            VaultService.permission?.let {
                originPermMap.put(uuid, permission, it.playerHas(player, permission))
                submit {
                    if (has) {
                        it.playerAddTransient(player, permission)
                    } else {
                        it.playerRemoveTransient(player, permission)
                    }
                }
            }
            permissionMap.put(uuid, permission, has)
        }
    }

    override fun unrealize(entity: LivingEntity, data: BuffData) {
        if (entity !is Player) return
        originPermMap[entity.uniqueId]?.forEach { (permission, has) ->
            submit {
                if (has) {
                    VaultService.permission?.playerAddTransient(entity, permission)
                } else {
                    VaultService.permission?.playerRemoveTransient(entity, permission)
                }
            }
        }
        originPermMap.remove(entity.uniqueId)
    }

    companion object {

        private val permissionMap = BaseMap<UUID, BaseMap<String, Boolean>>()
        private val originPermMap = BaseMap<UUID, BaseMap<String, Boolean>>()

    }

    override fun serialize(): MutableMap<String, Any> {
        return linkedMapOf("type" to "permission", "permissions" to permissions)
    }

    override fun hashCode(): Int {
        var result = key.hashCode()
        result = 31 * result + permissions.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PermissionEffect

        if (key != other.key) return false
        if (permissions != other.permissions) return false

        return true
    }

}