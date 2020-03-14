package me.syari.ss.core

import me.syari.ss.core.auto.Event
import me.syari.ss.core.auto.OnDisable
import me.syari.ss.core.bossBar.CreateBossBar
import me.syari.ss.core.inventory.CreateInventory
import me.syari.ss.core.message.ConsoleLogger
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {
    companion object {
        lateinit var corePlugin: JavaPlugin
        lateinit var coreLogger: ConsoleLogger
        lateinit var console: ConsoleCommandSender
    }

    override fun onEnable() {
        corePlugin = this
        coreLogger = ConsoleLogger(this)
        console = server.consoleSender
        Event.register(this, CreateBossBar, CreateInventory)
    }

    override fun onDisable() {
        OnDisable.register(CreateBossBar)
    }
}