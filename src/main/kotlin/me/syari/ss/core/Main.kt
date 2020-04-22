package me.syari.ss.core

import me.syari.ss.core.auto.Event
import me.syari.ss.core.auto.OnDisable
import me.syari.ss.core.auto.OnEnable
import me.syari.ss.core.bossBar.CreateBossBar
import me.syari.ss.core.inventory.CreateInventory
import me.syari.ss.core.message.ConsoleLogger
import me.syari.ss.core.time.TimeScheduler
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {
    companion object {
        /**
         * コアプラグインのインスタンス
         */
        lateinit var corePlugin: JavaPlugin

        /**
         * コアプラグインのロガー
         */
        lateinit var coreLogger: ConsoleLogger

        /**
         * コンソール
         */
        lateinit var console: ConsoleCommandSender
    }

    override fun onEnable() {
        corePlugin = this
        coreLogger = ConsoleLogger(this)
        console = server.consoleSender
        OnEnable.register(TimeScheduler)
        Event.register(this, CreateBossBar, CreateInventory, TimeScheduler)
    }

    override fun onDisable() {
        OnDisable.register(CreateBossBar)
    }
}