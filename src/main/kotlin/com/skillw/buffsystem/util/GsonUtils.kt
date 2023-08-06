package com.skillw.buffsystem.util

import com.google.gson.Gson

object GsonUtils {
    private val gson by lazy {
        Gson()
    }

    @JvmStatic
    fun String.parseToMap(): Map<String, Any> {
        return runCatching { gson.fromJson(this, Map::class.java) }.getOrNull()?.mapKeys { it.key.toString() }
            ?.mapValues { it.value.toString() } ?: error("Wrong JSON: $this")
    }
}