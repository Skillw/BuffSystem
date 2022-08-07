package com.skillw.buffsystem

import com.skillw.buffsystem.api.manager.*
import com.skillw.buffsystem.internal.manager.BSConfig
import com.skillw.pouvoir.api.annotation.PouManager
import com.skillw.pouvoir.api.manager.ManagerData
import com.skillw.pouvoir.api.plugin.SubPouvoir
import org.bukkit.plugin.java.JavaPlugin
import taboolib.common.platform.Plugin
import taboolib.module.configuration.Config
import taboolib.module.configuration.ConfigFile
import taboolib.platform.BukkitPlugin
import taboolib.platform.util.onlinePlayers

object BuffSystem : Plugin(), SubPouvoir {

    /** Basic */

    override val key = "BuffSystem"
    override val plugin: JavaPlugin by lazy(LazyThreadSafetyMode.NONE) {
        BukkitPlugin.getInstance()
    }

    /** Config */
    @Config("config.yml")
    lateinit var config: ConfigFile

    /** Managers */

    override lateinit var managerData: ManagerData

    @JvmStatic
    @PouManager
    lateinit var configManager: BSConfig

    @JvmStatic
    @PouManager
    lateinit var conditionManager: ConditionManager

    @JvmStatic
    @PouManager
    lateinit var effectManager: EffectManager

    @JvmStatic
    @PouManager
    lateinit var buffManager: BuffManager

    @JvmStatic
    @PouManager
    lateinit var buffDataManager: BuffDataManager

    @JvmStatic
    @PouManager
    lateinit var attributeManager: AttributeManager

    override fun onLoad() {
        load()
    }

    override fun onEnable() {
        enable()
    }

    override fun onActive() {
        active()
    }

    override fun onDisable() {
        onlinePlayers.forEach { player ->
            player.activePotionEffects.forEach {
                player.removePotionEffect(it.type)
            }
        }
        disable()
    }


}