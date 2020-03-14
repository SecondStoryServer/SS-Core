package me.syari.ss.core.command

import me.syari.ss.core.Main.Companion.corePlugin
import org.bukkit.command.CommandSender

object RunCommand {
    fun runCommand(sender: CommandSender, cmd: String) {
        corePlugin.server.dispatchCommand(sender, cmd)
    }

    fun runCommandFromConsole(cmd: String) {
        runCommand(corePlugin.server.consoleSender, cmd)
    }
}