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
import taboolib.common5.Coerce
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
        createIfNotExists("effects", "example.yml")
        createIfNotExists("scripts", "BossbarEffect.js")
        subReload()
        Metrics(16046, BukkitPlugin.getInstance().description.version, Platform.BUKKIT).run {
            addCustomChart(SingleLineChart("effects") {
                BuffSystem.effectManager.size
            })
            addCustomChart(SingleLineChart("buffs") {
                BuffSystem.buffManager.size
            })
        }
        Pouvoir.scriptEngineManager.globalVariables["BuffAPI"] = BuffAPI::class.java.static()
    }


    override fun subReload() {
        Pouvoir.scriptManager.addScriptDir(File(getDataFolder(), "scripts"))
    }

    val updateTime
        get() = this["config"].getLong("options.update-time")

    val permDescription: String
        get() = this["config"]["conditions.permission.description"].toString()
    val enablePermStatus: Boolean
        get() = Coerce.toBoolean(this["config"]["conditions.permission.status.enable"])
    val permStatusFormat: String
        get() = this["config"]["conditions.permission.status.format"].toString()
    val permEachFormat: String
        get() = this["config"]["conditions.permission.status.each"].toString()
    val permSeparator: String
        get() = this["config"]["conditions.permission.status.separator"].toString()

    val timeDescription: String
        get() = this["config"]["conditions.time.description"].toString()
    val enableTimeStatus: Boolean
        get() = Coerce.toBoolean(this["config"]["conditions.time.status.enable"])
    val timeStatusFormat: String
        get() = this["config"]["conditions.time.status.format"].toString()
    val timeMode: String
        get() = this["config"]["conditions.time.status.mode"].toString()
    val unlimited: String
        get() = this["config"]["conditions.time.status.unlimited"].toString()
    val second: String
        get() = this["config"]["conditions.time.status.second"].toString()
    val tick: String
        get() = this["config"]["conditions.time.status.tick"].toString()
    val dataClearSchedule: Long
        get() = Coerce.toLong(this["config"]["options.clear-time"])

}