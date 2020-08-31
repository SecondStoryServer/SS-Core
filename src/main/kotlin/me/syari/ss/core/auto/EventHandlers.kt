package me.syari.ss.core.auto

import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

/**
 * イベント登録
 */
interface EventHandlers: Listener {
    companion object {
        /**
         * ```
         * override fun onEnable(){
         *      EventHandlers.register(this, ...)
         * }
         * ```
         *
         * @param plugin イベント登録するプラグイン
         * @param event registerEvents を実行するクラス
         * @see org.bukkit.plugin.PluginManager.registerEvents
         */
        fun register(
            plugin: JavaPlugin,
            vararg event: EventHandlers
        ) {
            event.forEach {
                plugin.server.pluginManager.registerEvents(it, plugin)
            }
        }
    }
}