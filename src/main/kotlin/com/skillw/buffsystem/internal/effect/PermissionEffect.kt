package com.skillw.buffsystem.internal.effect

import com.skillw.buffsystem.api.data.BuffData
import com.skillw.buffsystem.api.effect.BaseEffect
import com.skillw.buffsystem.api.event.EffectLoadEvent
import com.skillw.pouvoir.Pouvoir
import com.skillw.pouvoir.api.map.BaseMap
import com.skillw.pouvoir.util.MapUtils.put
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common5.Coerce
import taboolib.platform.compat.VaultService
import java.util.*

class PermissionEffect(override val key: String, val permissions: List<String>) : BaseEffect(),
    ConfigurationSerializable {

    init {
        release = true
    }

    override fun realize(entity: LivingEntity, data: BuffData) {
        if (entity !is Player) return
        val player = entity
        val uuid = player.uniqueId
        val permissionStr = data.replace(this.permissions).map { Pouvoir.pouPlaceHolderAPI.replace(player, it) }
        permissionMap[uuid]?.clear()
        unrealize(entity, data)
        for (it in permissionStr) {
            val array = it.split(":")
            val permission = array[0]
            val has = Coerce.toBoolean(array[1])
            VaultService.permission?.let {
                originPermMap.put(uuid, permission, it.playerHas(player, permission))
                if (has) {
                    it.playerAddTransient(player, permission)
                } else {
                    it.playerRemoveTransient(player, permission)
                }
            }
            permissionMap.put(uuid, permission, has)
        }
    }

    override fun unrealize(entity: LivingEntity, data: BuffData) {
        if (entity !is Player) return
        originPermMap[entity.uniqueId]?.forEach { (permission, has) ->
            if (has) {
                VaultService.permission?.playerAddTransient(entity, permission)
            } else {
                VaultService.permission?.playerRemoveTransient(entity, permission)
            }
        }
        originPermMap.remove(entity.uniqueId)
    }

    companion object {

        @SubscribeEvent
        fun load(event: EffectLoadEvent) {
            event.result = deserialize(event.section) ?: return
        }

        private val permissionMap = BaseMap<UUID, BaseMap<String, Boolean>>()
        private val originPermMap = BaseMap<UUID, BaseMap<String, Boolean>>()

        @JvmStatic
        fun deserialize(section: ConfigurationSection): PermissionEffect? {
            try {
                val key = section.name
                if (section["type"].toString().lowercase() != "permission") return null
                val permissions = section.getStringList("permissions")
                return PermissionEffect(key, permissions).apply { config = true }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }
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