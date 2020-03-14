package me.syari.ss.core

import me.syari.ss.core.auto.EventInit
import me.syari.ss.core.auto.OnDisable
import me.syari.ss.core.bossBar.CreateBossBar
import me.syari.ss.core.inventory.CreateInventory
import me.syari.ss.core.message.ConsoleLogger
import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {
    companion object {
        lateinit var corePlugin: JavaPlugin
        lateinit var coreLogger: ConsoleLogger
    }

    override fun onEnable() {
        corePlugin = this
        coreLogger = ConsoleLogger(this)
        EventInit.register(this, CreateBossBar, CreateInventory)
    }

    override fun onDisable() {
        OnDisable.register(CreateBossBar)
    }
}