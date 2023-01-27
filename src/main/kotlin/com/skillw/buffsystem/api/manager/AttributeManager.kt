package com.skillw.buffsystem.api.manager

import com.skillw.buffsystem.BuffSystem
import com.skillw.buffsystem.api.attribute.AttributeProvider
import com.skillw.pouvoir.api.manager.Manager
import com.skillw.pouvoir.api.plugin.map.LowerKeyMap
import com.skillw.pouvoir.api.plugin.SubPouvoir
import taboolib.common.util.unsafeLazy

object AttributeManager : Manager, LowerKeyMap<AttributeProvider>() {
    override val key: String = "AttributeManager"
    override val priority: Int = 0
    override val subPouvoir: SubPouvoir = BuffSystem

    @JvmStatic
    val attrProvider by unsafeLazy {
        (values.firstOrNull() ?: error("No attribute provider found!"))
    }
}