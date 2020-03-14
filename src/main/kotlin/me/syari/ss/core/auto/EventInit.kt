package me.syari.ss.core.auto

import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

interface EventInit : Listener {
    companion object {
        fun register(plugin: JavaPlugin, vararg event: EventInit) {
            event.forEach {
                plugin.server.pluginManager.registerEvents(it, plugin)
            }
        }
    }
}