package me.syari.ss.core.code

import org.bukkit.ChatColor

object StringEditor {
    val String.toColor get() : String = ChatColor.translateAlternateColorCodes('&', this)

    val String.toUncolor get() = ChatColor.stripColor(toColor) ?: this
}