package me.syari.ss.core.command.create

import me.syari.ss.core.Main.Companion.corePlugin
import org.bukkit.command.Command
import org.bukkit.command.CommandMap
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin

object CreateCommand {
    fun tab(vararg arg: String, tab: (CommandSender, CommandArgument) -> CommandTabElement?): CommandTab.Base {
        return CommandTab.Base(arg.toList(), tab)
    }

    fun flag(arg: String, vararg flag: Pair<String, CommandTabElement>): CommandTab.Flag {
        return CommandTab.Flag(arg, flag.toMap())
    }

    fun element(element: Collection<String>?): CommandTabElement {
        return CommandTabElement(element ?: listOf())
    }

    fun element(vararg element: String): CommandTabElement {
        return element(element.toList())
    }

    fun element(run: () -> Collection<String>?): CommandTabElement {
        return CommandTabElement(run.invoke() ?: listOf())
    }

    fun elementIf(
        condition: Boolean,
        element: Collection<String>?,
        unlessElement: Collection<String>? = listOf()
    ): CommandTabElement {
        return CommandTabElement(
            if (condition) element ?: listOf() else unlessElement ?: listOf()
        )
    }

    fun elementIf(
        condition: Boolean,
        vararg element: String,
        unlessElement: Collection<String>? = listOf()
    ): CommandTabElement {
        return elementIf(condition, element.toList(), unlessElement)
    }

    fun elementIfOp(
        sender: CommandSender,
        element: Collection<String>?,
        unlessElement: Collection<String>? = listOf()
    ): CommandTabElement {
        return elementIf(
            sender.isOp,
            element ?: listOf(),
            unlessElement
        )
    }

    fun elementIfOp(
        sender: CommandSender,
        vararg element: String,
        unlessElement: Collection<String>? = listOf()
    ): CommandTabElement {
        return elementIfOp(sender, element.toList(), unlessElement)
    }

    fun createCommand(
        plugin: JavaPlugin,
        label: String,
        messagePrefix: String,
        vararg tab: CommandTab,
        alias: Collection<String> = listOf(),
        execute: CommandMessage.(CommandSender, CommandArgument) -> Unit
    ) {
        registerCommand(
            plugin,
            object : Command(label, "", "/", alias.toList()) {
                override fun execute(sender: CommandSender, commandLabel: String, args: Array<out String>): Boolean {
                    val message = CommandMessage(messagePrefix, sender)
                    execute.invoke(
                        message, sender,
                        CommandArgument(args, message)
                    )
                    return true
                }

                override fun tabComplete(sender: CommandSender, alias: String, args: Array<out String>): List<String> {
                    val message = CommandMessage(messagePrefix, sender)
                    val tabList = mutableListOf<String>()
                    val joinArg = args.joinToString(separator = " ").toLowerCase()
                    val size = args.size - 1
                    tab.forEach { eachTab ->
                        when(eachTab){
                            is CommandTab.Base -> {
                                val element = eachTab.tab.invoke(
                                    sender,
                                    CommandArgument(args, message)
                                )?.element ?: return@forEach
                                if (eachTab.arg.isEmpty()) {
                                    if (size == 0) tabList.addAll(element.filter { it.toLowerCase().startsWith(joinArg) })
                                } else {
                                    eachTab.arg.forEach { eachArg ->
                                        val splitArg = eachArg.split("\\s+".toRegex())
                                        if (splitArg.size == size) {
                                            val completed = if (eachArg.contains('*')) {
                                                StringBuilder().apply {
                                                    splitArg.forEachIndexed { index, word ->
                                                        append("${if(word != "*") word else args[index]} ")
                                                    }
                                                }.toString().substringBeforeLast(" ")
                                            } else {
                                                eachArg
                                            }
                                            tabList.addAll(element.filter {
                                                "$completed $it".toLowerCase().startsWith(joinArg)
                                            })
                                        }
                                    }
                                }
                            }
                            is CommandTab.Flag -> {
                                val splitArg = eachTab.arg.split("\\s+".toRegex())
                                splitArg.forEachIndexed { index, split ->
                                    if(split != "*" && split.toLowerCase() != args.getOrNull(index)?.toLowerCase()) {
                                        return@forEach
                                    }
                                }
                                val enterText = args.getOrNull(args.size - 1) ?: return@forEach
                                if((size - splitArg.size) % 2 == 0){
                                    val element = eachTab.flag.keys.toMutableSet()
                                    for(index in splitArg.size until size step 2){
                                        element.remove(args[index].toLowerCase())
                                    }
                                    tabList.addAll(element.filter {
                                        it.toLowerCase().startsWith(enterText)
                                    })
                                } else {
                                    val element = eachTab.flag[args.getOrNull(args.size - 2)?.toLowerCase()]
                                    if(element != null){
                                        tabList.addAll(element.filter {
                                            it.toLowerCase().startsWith(enterText)
                                        })
                                    }
                                }
                            }
                        }
                    }
                    return tabList.sorted()
                }
            }
        )
    }

    private fun registerCommand(plugin: JavaPlugin, command: Command) {
        try {
            val commandMapField = plugin.server.javaClass.getDeclaredField("commandMap")
            commandMapField.isAccessible = true
            val commandMap = commandMapField.get(plugin.server) as CommandMap
            commandMap.register(plugin.name, command)
            commandMapField.isAccessible = false
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    val offlinePlayers get() = CommandTabElement(corePlugin.server.offlinePlayers.mapNotNull { it?.name })

    val onlinePlayers get() = CommandTabElement(corePlugin.server.onlinePlayers.mapNotNull { it?.name })
}