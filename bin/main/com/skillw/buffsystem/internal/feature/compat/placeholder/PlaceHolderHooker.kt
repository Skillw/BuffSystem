package com.skillw.buffsystem.internal.feature.compat.placeholder

import com.skillw.pouvoir.Pouvoir
import org.bukkit.entity.Player
import taboolib.platform.compat.PlaceholderExpansion


object PlaceHolderHooker : PlaceholderExpansion {

    override val identifier: String = "bs"

    override fun onPlaceholderRequest(player: Player?, args: String): String {
        return Pouvoir.placeholderManager.replace(player, "%bs_$args%")
    }
}
