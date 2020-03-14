package me.syari.ss.core.bossBar

import me.syari.ss.core.auto.EventInit
import me.syari.ss.core.auto.OnDisable
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

object CreateBossBar : EventInit, OnDisable {
    val barList = mutableListOf<CustomBossBar>()

    @EventHandler
    fun on(e: PlayerJoinEvent) {
        val player = e.player
        barList.forEach { it.onLogin(player) }
    }

    @EventHandler
    fun on(e: PlayerQuitEvent) {
        val player = e.player
        barList.forEach { it.onLogout(player) }
    }

    override fun onDisable() {
        barList.forEach { it.clearPlayer() }
    }

    fun createBossBar(title: String, color: BarColor, style: BarStyle, public: Boolean = false) =
        CustomBossBar(title, color, style, public)
}