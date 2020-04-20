package me.syari.ss.core.inventory.event

import me.syari.ss.core.event.CustomEvent
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory

/**
 * カスタムではないインベントリが開かれた際に呼び出される
 */
class NaturalInventoryOpenEvent(val player: Player, val inventory: Inventory) : CustomEvent()