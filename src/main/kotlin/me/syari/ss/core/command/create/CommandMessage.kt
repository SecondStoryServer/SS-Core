package me.syari.ss.core.command.create

import me.syari.ss.core.message.Message.send
import org.bukkit.command.CommandSender

class CommandMessage(private val prefix: String, private val sender: CommandSender) {
    fun sendError(errorMessage: ErrorMessage) {
        sendWithPrefix("&c${errorMessage.message}")
    }

    fun sendWithPrefix(message: String) {
        sender.send("&b[$prefix] &r$message")
    }

    fun sendHelp(vararg command: Pair<String, String>): SendHelpIfOp {
        sendList("コマンド一覧", command.map { "/${it.first} &7${it.second}" })
        return SendHelpIfOp(this)
    }

    class SendHelpIfOp(private val message: CommandMessage) {
        fun ifOp(vararg command: Pair<String, String>) {
            if (message.sender.isOp) {
                message.sendList("", command.map { "/${it.first} &7${it.second}" })
            }
        }
    }

    fun sendList(title: String, vararg element: String) {
        sendList(title, element.toList())
    }

    fun sendList(title: String = "", element: Collection<String>) {
        if (title.isNotEmpty()) sendWithPrefix("&f$title")
        sender.send(
            StringBuilder().apply {
                element.forEach {
                    appendln("&7- &a$it")
                }
            }
        )
    }
}