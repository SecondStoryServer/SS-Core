package me.syari.ss.core.auto

import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

/**
 * イベント登録
 */
interface Event: Listener {
    companion object {
        /**
         * ```
         * override fun onEnable(){
         *      Event.register(this, ...)
         * }
         * ```
         *
         * @param plugin イベント登録するプラグイン
         * @param event registerEvents を実行するクラス
         * @see org.bukkit.plugin.PluginManager.registerEvents
         */
        fun register(
            plugin: JavaPlugin,
            vararg event: Event
        ) {
            event.forEach {
                plugin.server.pluginManager.registerEvents(it, plugin)
            }
        }
    }
}