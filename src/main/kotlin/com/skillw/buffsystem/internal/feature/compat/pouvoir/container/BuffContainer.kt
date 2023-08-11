package com.skillw.buffsystem.internal.feature.compat.pouvoir.container

import com.skillw.buffsystem.internal.manager.BSConfig
import com.skillw.pouvoir.Pouvoir
import com.skillw.pouvoir.api.feature.database.UserBased
import com.skillw.pouvoir.api.plugin.annotation.AutoRegister
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake

@AutoRegister
object BuffContainer : UserBased {
    @JvmStatic
    val holder by lazy {
        Pouvoir.databaseManager.containerHolder(BSConfig.databaseConfig)
    }

    @JvmStatic
    lateinit var container: UserBased

    @Awake(LifeCycle.ENABLE)
    fun loadContainer() {
        kotlin.runCatching {
            container = (holder?.container("as_data", true) as? UserBased?)!!
        }.let {
            if (it.isFailure)
                taboolib.common.platform.function.warning("AttributeSystem User Container Initialization Failed!")
        }
    }

    override fun get(user: String, key: String): String? {
        return container[user, key]
    }

    override fun delete(user: String, key: String) {
        return container.delete(user, key)
    }

    override fun set(user: String, key: String, value: String?) {
        container[user, key] = value
    }

    override fun contains(user: String, key: String): Boolean {
        return container.contains(user, key)
    }
}