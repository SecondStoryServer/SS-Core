package me.syari.ss.core.bossBar

import me.syari.ss.core.Main.Companion.corePlugin
import me.syari.ss.core.bossBar.CreateBossBar.barList
import me.syari.ss.core.code.StringEditor.toColor
import org.bukkit.OfflinePlayer
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.boss.BossBar
import org.bukkit.entity.Player

class CustomBossBar(title: String, color: BarColor, style: BarStyle, private val public: Boolean) {
    private val bar: BossBar = corePlugin.server.createBossBar(title.toColor, color, style)

    init {
        if (public) {
            corePlugin.server.onlinePlayers.forEach { bar.addPlayer(it) }
        }
        barList.add(this)
    }

    private val addOnLogin = mutableListOf<OfflinePlayer>()

    fun containsPlayer(player: OfflinePlayer): Boolean {
        if (public) return true
        return if (player is Player) bar.players.contains(player) else addOnLogin.contains(player)
    }

    fun addPlayer(player: OfflinePlayer) {
        if (public || containsPlayer(player)) return
        if (player is Player) bar.addPlayer(player)
        else addOnLogin.add(player)
    }

    fun removePlayer(player: OfflinePlayer) {
        if (public || !containsPlayer(player)) return
        if (player is Player) bar.removePlayer(player)
        else addOnLogin.remove(player)
    }

    fun clearPlayer() {
        bar.removeAll()
    }

    var title
        get() = bar.title
        set(value) {
            bar.setTitle(value.toColor)
        }

    /*
    fun removeAllPlayer(players: Collection<OfflinePlayer>){
        players.forEach { player ->
            removePlayer(player)
        }
    }
    */

    fun onLogin(player: Player) {
        if (public) {
            bar.addPlayer(player)
        } else if (addOnLogin.contains(player)) {
            bar.addPlayer(player)
            addOnLogin.remove(player)
        }
    }

    fun onLogout(player: Player) {
        if (public) return
        bar.removePlayer(player)
        addOnLogin.add(player)
    }

    var progress
        get() = bar.progress
        set(value) {
            bar.progress = when {
                value < 0.0 -> 0.0
                1.0 < value -> 1.0
                else -> value
            }
        }

    fun delete() {
        barList.remove(this)
        clearPlayer()
    }
}