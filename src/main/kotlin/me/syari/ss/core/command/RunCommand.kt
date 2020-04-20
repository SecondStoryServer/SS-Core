package me.syari.ss.core.command

import me.syari.ss.core.Main.Companion.console
import me.syari.ss.core.Main.Companion.corePlugin
import org.bukkit.command.CommandSender

object RunCommand {
    /**
     * コマンドを実行します
     * @param sender 実行元
     * @param command 実行するコマンド
     */
    fun runCommand(sender: CommandSender, command: String) {
        corePlugin.server.dispatchCommand(sender, command)
    }

    /**
     * コンソールからコマンドを実行します
     * @param command 実行するコマンド
     */
    fun runCommandFromConsole(command: String) {
        runCommand(console, command)
    }
}