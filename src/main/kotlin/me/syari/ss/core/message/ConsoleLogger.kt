package me.syari.ss.core.message

import me.syari.ss.core.message.Message.send
import org.bukkit.plugin.java.JavaPlugin

class ConsoleLogger(private val plugin: JavaPlugin) {
    fun send(message: String) {
        plugin.server.consoleSender.send(message)
    }

    fun info(message: String) {
        plugin.server.logger.info(message)
    }

    fun warn(message: String) {
        plugin.server.logger.warning(message)
    }

    fun error(message: String) {
        plugin.server.logger.severe(message)
    }
}