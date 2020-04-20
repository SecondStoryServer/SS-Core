package me.syari.ss.core.player

import org.bukkit.Bukkit.getOfflinePlayer
import org.bukkit.Bukkit.getPlayer
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import java.util.*

data class UUIDPlayer(private val uniqueId: UUID) {
    constructor(player: OfflinePlayer) : this(player.uniqueId)

    /**
     * プレイヤーに変換します
     */
    val player get(): Player? = getPlayer(uniqueId)

    /**
     * オフラインプレイヤーに変換します
     */
    val offlinePlayer get(): OfflinePlayer? = getOfflinePlayer(uniqueId)

    /**
     * 名前を取得します
     */
    val name get() = offlinePlayer?.name

    /**
     * オンラインか取得します
     */
    val isOnline get() = offlinePlayer?.isOnline ?: false

    /**
     * UUIDを文字列として取得します
     */
    override fun toString() = uniqueId.toString()
}