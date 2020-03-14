package me.syari.ss.core.inventory

import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryEvent

data class InventoryPlayerData(
    val id: String,
    val cancel: Boolean,
    private val onEvent: ((InventoryEvent) -> Unit)?,
    private val onClick: ((InventoryClickEvent) -> Unit)?,
    private val onClose: ((InventoryCloseEvent) -> Unit)?,
    private val clickEvent: Map<Pair<Int, ClickType?>, () -> Unit>?
) {
    fun onClick(e: InventoryClickEvent) {
        onClick?.invoke(e)
    }

    fun onClose(e: InventoryCloseEvent) {
        onClose?.invoke(e)
    }

    fun runEvent(index: Int, click: ClickType) {
        clickEvent?.run {
            get(index to click)?.invoke()
            get(index to null)?.invoke()
        }
    }
}