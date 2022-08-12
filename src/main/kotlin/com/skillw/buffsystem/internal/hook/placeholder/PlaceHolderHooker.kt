package com.skillw.buffsystem.internal.hook.placeholder

import com.skillw.pouvoir.Pouvoir
import org.bukkit.entity.Player
import taboolib.platform.compat.PlaceholderExpansion


object PlaceHolderHooker : PlaceholderExpansion {

    override val identifier: String = "bs"

    override fun onPlaceholderRequest(player: Player?, args: String): String {
        return Pouvoir.pouPlaceHolderAPI.replace(player, "%bs_$args%")
    }
}
