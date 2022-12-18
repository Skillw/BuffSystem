package com.skillw.buffsystem.internal.feature.compat.pouvoir.functions

import com.skillw.buffsystem.internal.feature.compat.mythic.BuffHandler.handle
import com.skillw.pouvoir.api.annotation.AutoRegister
import com.skillw.pouvoir.api.function.PouFunction
import com.skillw.pouvoir.api.function.parser.Parser
import org.bukkit.entity.LivingEntity

@AutoRegister
object FunctionBuff : PouFunction<Any>("buff") {
    override fun execute(parser: Parser): Any {
        with(parser) {
            val entity = parse<LivingEntity>()
            val type = parseString()
            var key = ""
            var buffKey = ""
            var json = ""
            val data = HashMap<String, Any>()
            when (type) {
                "add" -> {
                    key = parseString()
                    buffKey = parseString()
                    if (except("with")) {
                        data.putAll(parseMap())
                    }
                }

                "remove" -> {
                    key = parseString()
                }

                "removeIf" -> {
                    json = parseString()
                }

                "matches" -> {
                    json = parseString()
                }
            }
            return entity.handle(type, key, buffKey, json, data)
        }
    }
}