package com.skillw.buffsystem.internal.feature.compat.pouvoir.functions

import com.skillw.asahi.api.annotation.AsahiPrefix
import com.skillw.asahi.api.member.quest.Quester
import com.skillw.asahi.api.prefixParser
import com.skillw.asahi.api.quest
import com.skillw.asahi.api.quester
import com.skillw.buffsystem.internal.feature.compat.mythic.BuffHandler.handle
import org.bukkit.entity.LivingEntity


@AsahiPrefix(["buff"], "common")
fun buff() = prefixParser<Boolean> {
    val entity = quest<LivingEntity>()
    val type = next()
    var key: Quester<String> = quester { "" }
    var buffKey: Quester<String> = quester { "" }
    var json: Quester<String> = quester { "" }
    var data: Quester<Map<String, Any>> = quester { emptyMap() }
    when (type) {
        "add" -> {
            key = quest()
            buffKey = quest()
            if (expect("with")) {
                data = quest()
            }
        }

        "remove" -> {
            key = quest()
        }

        "removeIf" -> {
            json = quest()
        }

        "matches" -> {
            json = quest()
        }
    }
    result {
        entity.get().handle(type, key.get(), buffKey.get(), json.get(), data.get())
    }
}