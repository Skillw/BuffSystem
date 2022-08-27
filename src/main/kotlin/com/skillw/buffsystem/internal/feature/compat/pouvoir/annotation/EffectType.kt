package com.skillw.buffsystem.internal.feature.compat.pouvoir.annotation

import com.skillw.buffsystem.BuffSystem
import com.skillw.buffsystem.api.data.BuffData
import com.skillw.buffsystem.api.effect.BaseEffect
import com.skillw.buffsystem.api.effect.EffectBuilder
import com.skillw.pouvoir.Pouvoir
import com.skillw.pouvoir.api.annotation.AutoRegister
import com.skillw.pouvoir.api.script.annotation.ScriptAnnotation
import com.skillw.pouvoir.api.script.annotation.ScriptAnnotationData
import com.skillw.pouvoir.internal.manager.PouConfig
import com.skillw.pouvoir.util.StringUtils.toArgs
import org.bukkit.entity.LivingEntity
import taboolib.common.platform.function.console
import taboolib.module.lang.sendLang

/**
 * EffectType 效果类型注解 (是文件注解 请在脚本文件第一行写)
 *
 * @constructor EffectType Key: String
 */
@AutoRegister
object EffectType : ScriptAnnotation("EffectType", fileAnnotation = true) {
    override fun handle(data: ScriptAnnotationData) {
        val script = data.script
        val args = data.args.toArgs()
        val function = data.function
        if (function != "null") return
        if (args.isEmpty()) return
        val key = args[0]
        object : EffectBuilder(key) {
            override fun build(key: String, map: Map<String, Any>): BaseEffect? {
                if (map["type"].toString().lowercase() != this.key) return null
                return object : BaseEffect(key) {
                    override fun realize(entity: LivingEntity, data: BuffData) {
                        Pouvoir.scriptManager.invoke<Unit>(script, "realize", parameters = arrayOf(entity, data, map))
                    }

                    override fun unrealize(entity: LivingEntity, data: BuffData) {
                        Pouvoir.scriptManager.invoke<Unit>(script, "unrealize", parameters = arrayOf(entity, data, map))
                    }

                    override fun hashCode(): Int {
                        return script.hashCode()
                    }

                    override fun serialize(): MutableMap<String, Any> {
                        return map.toMutableMap()
                    }
                }
            }
        }.register()
        PouConfig.debug { console().sendLang("annotation-effect-type-register", key) }
        script.onDeleted("Buff-EffectType-Type-$key") {
            PouConfig.debug { console().sendLang("annotation-effect-type-unregister", key) }
            BuffSystem.effectBuilderManager.remove(key)
        }
    }
}