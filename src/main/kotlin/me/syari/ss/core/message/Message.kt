package me.syari.ss.core.message

import me.syari.ss.core.Main.Companion.coreLogger
import me.syari.ss.core.Main.Companion.corePlugin
import me.syari.ss.core.code.StringEditor.toColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object Message {
    fun broadcast(message: String) {
        corePlugin.server.onlinePlayers.forEach {
            it.sendMessage(message)
        }
        coreLogger.send(message)
    }

    fun send(message: String, vararg to: CommandSender) {
        val colored = message.toColor
        to.forEach {
            it.sendMessage(colored)
        }
    }

    fun CommandSender.send(message: String) {
        send(message, this)
    }

    fun CommandSender.send(builder: StringBuilder) {
        send(builder.toString())
    }

    fun Player.title(main: String, sub: String, fadeIn: Int, stay: Int, fadeOut: Int) {
        sendTitle(main.toColor, sub.toColor, fadeIn, stay, fadeOut)
    }

    fun Player.action(message: String) {
        sendActionBar(message.toColor)
    }
}