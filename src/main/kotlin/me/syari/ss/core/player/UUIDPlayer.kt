package me.syari.ss.core.player

import org.bukkit.Bukkit.getOfflinePlayer
import org.bukkit.Bukkit.getPlayer
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import java.util.*

data class UUIDPlayer(private val uniqueId: UUID) {
    constructor(player: OfflinePlayer) : this(player.uniqueId)

    val player get(): Player? = getPlayer(uniqueId)

    val offlinePlayer get(): OfflinePlayer? = getOfflinePlayer(uniqueId)

    val name get() = offlinePlayer?.name

    val isOnline get() = offlinePlayer?.isOnline ?: false

    override fun toString() = uniqueId.toString()
}