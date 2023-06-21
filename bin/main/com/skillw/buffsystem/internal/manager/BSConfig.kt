package com.skillw.buffsystem.internal.manager

import com.skillw.buffsystem.BuffSystem
import com.skillw.buffsystem.api.BuffAPI
import com.skillw.pouvoir.Pouvoir
import com.skillw.pouvoir.api.manager.ConfigManager
import com.skillw.pouvoir.api.plugin.map.DataMap
import com.skillw.pouvoir.util.static
import com.skillw.pouvoir.util.toMap
import taboolib.common.platform.Platform
import taboolib.common.platform.function.getDataFolder
import taboolib.module.metrics.Metrics
import taboolib.module.metrics.charts.SingleLineChart
import taboolib.platform.BukkitPlugin
import java.io.File


object BSConfig : ConfigManager(BuffSystem) {
    override val priority = 0

    val databaseConfig: DataMap
        get() = DataMap().also { it.putAll(this["config"].getConfigurationSection("database")!!.toMap()) }

    override fun onLoad() {
        createIfNotExists("buffs", "example.yml")
        subReload()
        Metrics(16046, BukkitPlugin.getInstance().description.version, Platform.BUKKIT).run {

            addCustomChart(SingleLineChart("buffs") {
                BuffSystem.buffManager.size
            })
        }
        Pouvoir.scriptEngineManager.globalVariables["BuffAPI"] = BuffAPI::class.java.static()
    }


    override fun subReload() {
        Pouvoir.scriptManager.addScriptDir(File(getDataFolder(), "scripts"))
    }

    val second: String
        get() = this["config"]["conditions.time.status.second"].toString()

}