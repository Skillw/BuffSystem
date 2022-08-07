package com.skillw.buffsystem.internal.annotation

import com.skillw.buffsystem.internal.script.EffectScript
import com.skillw.pouvoir.api.annotation.AutoRegister
import com.skillw.pouvoir.api.script.annotation.ScriptAnnotation
import com.skillw.pouvoir.api.script.annotation.ScriptAnnotationData
import com.skillw.pouvoir.internal.manager.PouConfig
import com.skillw.pouvoir.util.StringUtils.toArgs
import taboolib.common.platform.function.console
import taboolib.module.lang.sendLang

/**
 * EffectType 读取格式注解 (是文件注解 请在脚本文件第一行写)
 *
 * @constructor EffectType Key: String
 */
@AutoRegister
object EffectType : ScriptAnnotation("EffectType") {
    override fun handle(data: ScriptAnnotationData) {
        val script = data.script
        val args = data.args.toArgs()
        val function = data.function
        if (function != "null") return
        if (args.isEmpty()) return
        val key = args[0]
        EffectScript(key, script).register()
        PouConfig.debug { console().sendLang("annotation-effect-type-register", key) }
        script.onDeleted("Buff-EffectType-Type-$key") {
            PouConfig.debug { console().sendLang("annotation-effect-type-unregister", key) }
            EffectScript.effectScripts.remove(key)
        }
    }
}