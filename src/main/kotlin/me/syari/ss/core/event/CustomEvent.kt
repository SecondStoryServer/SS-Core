package me.syari.ss.core.event

import org.bukkit.event.Event
import org.bukkit.event.HandlerList

open class CustomEvent : Event() {
    override fun getHandlers(): HandlerList {
        return HANDLERS
    }

    companion object {
        private val HANDLERS = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList {
            return HANDLERS
        }
    }
}