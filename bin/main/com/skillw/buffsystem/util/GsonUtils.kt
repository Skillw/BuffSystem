package com.skillw.buffsystem.util

import com.google.gson.Gson

object GsonUtils {
    @JvmStatic
    fun String.parseToMap(): Map<String, Any> {
        return runCatching { Gson().fromJson(this, Map::class.java) }.getOrNull()?.mapKeys { it.key.toString() }
            ?.mapValues { it.value.toString() } ?: error("Wrong JSON: $this")
    }
}