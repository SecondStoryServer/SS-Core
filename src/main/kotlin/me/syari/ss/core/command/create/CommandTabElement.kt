package me.syari.ss.core.command.create

import org.bukkit.command.CommandSender

class CommandTabElement(list: Collection<String>) : Collection<String> {
    var element = list.toSet()
        private set

    override val size: Int
        get() = element.size

    private fun joinList(element: Collection<String>): CommandTabElement {
        this.element = this.element.union(element)
        return this
    }


    /**
     * 要素を追加します
     * @param element 追加する要素
     */
    fun join(vararg element: String): CommandTabElement {
        return joinList(element.toList())
    }

    /**
     * [condition] が 真 だった場合に 要素を追加します
     * @param condition 条件
     * @param element 条件に一致した場合追加する要素
     */
    fun joinIf(condition: Boolean, vararg element: String): CommandTabElement {
        return if (condition) joinList(element.toList()) else this
    }

    /**
     * [sender] が OP だった場合に 要素を追加します
     * @param sender CommandSender
     * @param element sender.isOpが真だった場合追加する要素
     */
    fun joinIfOp(sender: CommandSender, vararg element: String): CommandTabElement {
        return joinIf(sender.isOp, *element)
    }

    /**
     * @param sender CommandSender
     * @param element sender.isOpが偽だった場合追加する要素
     */
    fun joinIfNotOp(sender: CommandSender, vararg element: String): CommandTabElement {
        return joinIf(!sender.isOp, *element)
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