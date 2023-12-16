package com.skillw.buffsystem.internal.core.asahi.prefix

import com.skillw.asahi.api.annotation.AsahiPrefix
import com.skillw.asahi.api.prefixParser
import com.skillw.asahi.api.quest
import com.skillw.asahi.api.quester
import com.skillw.buffsystem.BuffSystem.attributeManager
import com.skillw.buffsystem.BuffSystem.buffDataManager
import com.skillw.buffsystem.api.data.BuffData
import com.skillw.buffsystem.internal.feature.compat.mythic.BuffHandler.handle
import com.skillw.buffsystem.internal.manager.BuffDataManagerImpl.reloading
import org.bukkit.entity.LivingEntity


@AsahiPrefix(["buff"])
private fun buff() = prefixParser<Any> {
    val type = next()
    var key = quester { "" }
    var buffKey = quester { "" }
    var json = quester { "" }
    var data = quester { HashMap<String, Any>() }
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
    result { selector<LivingEntity>().handle(type, key.get(), buffKey.get(), json.get(), data.get()) }
}


@AsahiPrefix(["removeBuff"], namespace = "buff")
private fun removeBuff() = prefixParser<Any> {
    result {
        if (reloading) return@result
        val data = get("data") as? BuffData ?: return@result
        data.task?.cancel()
        data.task = null
        data.exit()
        data["removing"] = true
        buffDataManager[data.entity.uniqueId]?.remove(data.key)
    }
}

@AsahiPrefix(["buff-att"], namespace = "buff")
private fun buffAttr() = prefixParser<Any> {
    when (val type = next()) {
        "add" -> {
            val source = quest<String>()
            val strings = quest<List<Any?>>().quester { it.map { each -> each.toString() } }
            val entityGetter = if (expect("to")) quest<LivingEntity>() else quester { selector() }
            result {
                attributeManager.attrProvider.addAttribute(entityGetter.get(), source.get(), strings.get())
            }
        }

        "remove" -> {
            val source = quest<String>()
            val entityGetter = if (expect("to")) quest<LivingEntity>() else quester { selector() }
            result {
                attributeManager.attrProvider.removeAttribute(entityGetter.get(), source.get())
            }
        }

        else -> {
            error("Invalid Attr token $type")
        }
    }
}
