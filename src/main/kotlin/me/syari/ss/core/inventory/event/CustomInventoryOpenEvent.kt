package me.syari.ss.core.inventory.event

import me.syari.ss.core.event.CustomEvent
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory

class CustomInventoryOpenEvent(val player: Player, val inventory: Inventory) : CustomEvent()