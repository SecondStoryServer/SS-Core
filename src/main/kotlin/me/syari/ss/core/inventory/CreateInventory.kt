package me.syari.ss.core.inventory

import me.syari.ss.core.Main.Companion.corePlugin
import me.syari.ss.core.auto.Event
import me.syari.ss.core.code.StringEditor.toColor
import me.syari.ss.core.inventory.CreateInventory.runWithId
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

object CreateInventory : Event {
    @EventHandler
    fun onInventoryOpen(e: InventoryOpenEvent) {
        val player = e.player as Player
        val inventory = e.inventory
        if (player.menuPlayer != null) {
            CustomInventoryOpenEvent(player, inventory)
        } else {
            NaturalInventoryOpenEvent(player, inventory)
        }.callEvent()
    }

    @EventHandler
    fun onInventoryClick(e: InventoryClickEvent) {
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
    fun onInventoryClose(e: InventoryCloseEvent) {
        val player = e.player as Player
        val playerData = player.menuPlayer ?: return
        playerData.onClose(e)
        player.menuPlayer = null
        runLater(corePlugin, 5) {
            player.updateInventory()
        }
    }

    /**
     * @param inventory [Inventory]
     * @param id インベントリのID
     * @return [CustomInventory]
     */
    fun inventory(inventory: Inventory, vararg id: String): CustomInventory {
        return CustomInventory(inventory, id.toList())
    }

    /**
     * @param display インベントリのタイトル
     * @param type インベントリの種類
     * @param id インベントリのID
     * @return [CustomInventory]
     */
    fun inventory(display: String, type: InventoryType, vararg id: String): CustomInventory {
        return inventory(createInventory(null, type, display.toColor), *id)
    }

    /**
     * @param display インベントリのタイトル
     * @param type インベントリの種類
     * @param id インベントリのID
     * @param run インベントリに対して実行する処理
     * @return [CustomInventory]
     */
    fun inventory(
        display: String,
        type: InventoryType,
        vararg id: String,
        run: CustomInventory.() -> Unit
    ): CustomInventory {
        return inventory(display, type, *id).apply(run)
    }

    /**
     * @param display インベントリのタイトル
     * @param line インベントリの行数 default: 3
     * @param id インベントリのID
     * @param run インベントリに対して実行する処理
     * @return [CustomInventory]
     */
    fun inventory(
        display: String,
        line: Int = 3,
        vararg id: String,
        run: CustomInventory.() -> Unit
    ): CustomInventory {
        return inventory(createInventory(null, (if (line in 1..6) line else 3) * 9, display.toColor), *id).apply(run)
    }

    private val menuPlayers = mutableMapOf<UUIDPlayer, InventoryPlayerData>()

    /**
     * インベントリのプレイヤーデータ
     */
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

    /**
     * [id] から始まる インベントリID を持つプレイヤーに処理を行います
     * @param id インベントリのID
     * @param run プレイヤーに対して実行する処理
     */
    fun runWithId(vararg id: String, run: (Player) -> Unit) {
        val joinedId = id.joinToString("-").toColor
        menuPlayers.forEach { (uuidPlayer, playerData) ->
            if (playerData.id.startsWith(joinedId)) {
                val player = uuidPlayer.player ?: return@forEach
                run.invoke(player)
            }
        }
    }

    /**
     * 該当プレイヤーに再度インベントリを開かせます
     * @see runWithId
     * @param id インベントリのID
     * @param inventory プレイヤーに開かせるインベントリ
     */
    fun reopen(vararg id: String, inventory: CustomInventory) {
        runWithId(*id) {
            inventory.open(it)
        }
    }

    /**
     * 該当プレイヤーに再度インベントリを開かせます
     * @see runWithId
     * @param id インベントリのID
     * @param run プレイヤーに対して実行する処理
     */
    fun reopen(vararg id: String, run: (Player) -> CustomInventory) {
        runWithId(*id) {
            run.invoke(it).open(it)
        }
    }

    /**
     * 該当プレイヤーのインベントリを閉じます
     * @see runWithId
     * @param id インベントリのID
     */
    fun close(vararg id: String) {
        runWithId(*id) {
            it.closeInventory()
        }
    }

    /**
     * @param humanEntity インベントリを閉じるプレイヤー
     */
    fun close(humanEntity: HumanEntity) {
        humanEntity.closeInventory()
    }
}