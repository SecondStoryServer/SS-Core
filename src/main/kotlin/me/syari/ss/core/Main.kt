package me.syari.ss.core

import me.syari.ss.core.bossBar.CreateBossBar
import me.syari.ss.core.code.SSPlugin
import me.syari.ss.core.inventory.CreateInventory
import me.syari.ss.core.message.ConsoleLogger
import me.syari.ss.core.time.TimeScheduler
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.plugin.java.JavaPlugin

class Main: SSPlugin() {
    companion object {
        /**
         * コアプラグインのインスタンス
         */
        internal lateinit var corePlugin: JavaPlugin

        /**
         * コアプラグインのロガー
         */
        internal lateinit var coreLogger: ConsoleLogger

        /**
         * コンソール
         */
        lateinit var console: ConsoleCommandSender
    }

    override val listeners = listOf(CreateBossBar, CreateInventory, TimeScheduler)
    override val onEnables = listOf(TimeScheduler)
    override val onDisables = listOf(CreateBossBar)

    override fun onEnable() {
        corePlugin = this
        coreLogger = ConsoleLogger(this)
        console = server.consoleSender
        runOnEnable()
        registerListeners()
    }

    override fun onDisable() {
        runOnDisable()
    }
}