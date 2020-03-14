package me.syari.ss.core.inventory

import me.syari.ss.core.Main.Companion.corePlugin
import me.syari.ss.core.auto.EventInit
import me.syari.ss.core.code.StringEditor.toColor
import me.syari.ss.core.inventory.event.CustomInventoryOpenEvent
import me.syari.ss.core.inventory.event.NaturalInventoryOpenEvent
import me.syari.ss.core.player.UUIDPlayer
import me.syari.ss.core.scheduler.CustomScheduler.runLater
import org.bukkit.Bukkit.createInventory
import org.bukkit.OfflinePlayer
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.*
import org.bukkit.inventory.Inventory

object CreateInventory : EventInit {
    @EventHandler
    fun on(e: InventoryOpenEvent) {
        val player = e.player as Player
        val inventory = e.inventory
        if (player.menuPlayer != null) {
            CustomInventoryOpenEvent(player, inventory)
        } else {
            NaturalInventoryOpenEvent(player, inventory)
        }.callEvent()
    }

    @EventHandler
    fun on(e: InventoryClickEvent) {
        val player = e.whoClicked as Player
        val playerData = player.menuPlayer ?: return
        if (playerData.cancel) {
            e.isCancelled = true
        }
        if (e.inventory == e.clickedInventory) {
            playerData.runEvent(e.slot, e.click)
        }
        playerData.onClick(e)
        if (e.click == ClickType.MIDDLE && player.isOp) {
            val item = e.currentItem
            if (item != null) {
                e.isCancelled = true
                player.inventory.addItem(item.asQuantity(64))
            }
        }
    }

    @EventHandler
    fun on(e: InventoryCloseEvent) {
        val player = e.player as Player
        val playerData = player.menuPlayer ?: return
        playerData.onClose(e)
        player.menuPlayer = null
        runLater(corePlugin, 5) {
            player.updateInventory()
        }
    }

    fun inventory(inventory: Inventory, vararg id: String): CustomInventory {
        return CustomInventory(inventory, id.toList())
    }

    fun inventory(display: String, type: InventoryType, vararg id: String): CustomInventory {
        return inventory(createInventory(null, type, display.toColor), *id)
    }

    fun inventory(display: String, type: InventoryType, vararg id: String, run: CustomInventory.() -> Unit): CustomInventory {
        return inventory(display, type, *id).apply(run)
    }

    fun inventory(display: String, line: Int = 3, vararg id: String, run: CustomInventory.() -> Unit): CustomInventory {
        return inventory(createInventory(null, (if (line in 1..6) line else 3) * 9, display.toColor), *id).apply(run)
    }

    private val menuPlayers = mutableMapOf<UUIDPlayer, InventoryPlayerData>()

    var OfflinePlayer.menuPlayer
        get() = menuPlayers[UUIDPlayer(this)]
        set(value) {
            val uuidPlayer = UUIDPlayer(this)
            if (value != null) {
                menuPlayers[uuidPlayer] = value
            } else {
                menuPlayers.remove(uuidPlayer)
            }
        }

    fun reopen(vararg id: String, run: (Player) -> Unit) {
        val joinedId = id.joinToString("-").toColor
        menuPlayers.forEach { (uuidPlayer, playerData) ->
            if (playerData.id.startsWith(joinedId)) {
                val player = uuidPlayer.player ?: return@forEach
                run.invoke(player)
            }
        }
    }

    fun close(humanEntity: HumanEntity) {
        humanEntity.closeInventory()
    }

    fun close(vararg id: String) {
        val joinedId = id.joinToString("-").toColor
        menuPlayers.forEach { (uuidPlayer, playerData) ->
            if (playerData.id.startsWith(joinedId)) {
                uuidPlayer.player?.closeInventory()
            }
        }
    }
}