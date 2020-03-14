package me.syari.ss.core.command.create

import org.bukkit.command.CommandSender

sealed class CommandTab {
    class Base(val arg: List<String>, val tab: (CommandSender, CommandArgument) -> CommandTabElement?) : CommandTab()
    class Flag(val arg: String, val flag: Map<String, CommandTabElement>): CommandTab()
}