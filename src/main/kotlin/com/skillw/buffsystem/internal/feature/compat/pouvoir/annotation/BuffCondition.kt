package com.skillw.buffsystem.internal.feature.compat.pouvoir.annotation

import com.skillw.attsystem.AttributeSystem
import com.skillw.buffsystem.internal.core.condition.ScriptBuffCondition
import com.skillw.pouvoir.api.plugin.annotation.AutoRegister
import com.skillw.pouvoir.api.script.annotation.ScriptAnnotation
import com.skillw.pouvoir.api.script.annotation.ScriptAnnotationData
import com.skillw.pouvoir.internal.manager.PouConfig
import com.skillw.pouvoir.util.toArgs
import taboolib.common.platform.function.console
import taboolib.module.lang.sendLang

/**
 * BuffCondition 读取格式注解 (是文件注解 请在脚本文件第一行写) BuffCondition键
 *
 * @constructor Buff Condition Key: String
 */
@AutoRegister
object BuffCondition : ScriptAnnotation("BuffCondition", fileAnnotation = true) {
    override fun handle(data: ScriptAnnotationData) {
        val script = data.script
        val args = data.args.toArgs()
        val function = data.function
        if (function != "null") return
        if (args.isEmpty()) return
        val key = args[0]
        ScriptBuffCondition(key, script).register()
        PouConfig.debug { console().sendLang("annotation-buff-condition-register", key) }
        script.onDeleted("Buff-Condition-$key") {
            PouConfig.debug { console().sendLang("annotation-buff-condition-unregister", key) }
            AttributeSystem.conditionManager.remove(key)
        }
    }
}