package me.syari.ss.core.command.create

import org.bukkit.command.CommandSender

class CommandTabElement(list: Collection<String>) : Collection<String> {
    var element = list.toSet()
        private set

    override val size: Int
        get() = element.size

    private fun joinList(run: () -> Collection<String>?): CommandTabElement {
        run.invoke()?.let { element = element.union(it) }
        return this
    }

    fun joinIf(bool: Boolean, vararg values: String): CommandTabElement {
        return if (bool) joinList { values.toList() } else this
    }

    fun joinIfOp(sender: CommandSender, vararg values: String): CommandTabElement {
        return joinIf(sender.isOp, *values)
    }

    override fun contains(element: String): Boolean {
        return this.element.contains(element)
    }

    override fun containsAll(elements: Collection<String>): Boolean {
        return element.containsAll(elements)
    }

    override fun isEmpty(): Boolean {
        return element.isEmpty()
    }

    override fun iterator(): Iterator<String> {
        return element.iterator()
    }
}