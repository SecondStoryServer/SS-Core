package me.syari.ss.core.inventory

import me.syari.ss.core.code.StringEditor.toColor
import me.syari.ss.core.inventory.CreateInventory.menuPlayer
import me.syari.ss.core.item.CustomItemStack
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

class CustomInventory(val inventory: Inventory, private val id: List<String>) {
    private val events = mutableMapOf<Pair<Int, ClickType?>, () -> Unit>()
    var cancel = true
    var onEvent: ((InventoryEvent) -> Unit)? = null
    var onClick: ((InventoryClickEvent) -> Unit)? = null
    var onClose: ((InventoryCloseEvent) -> Unit)? = null
    var contents: Array<ItemStack>
        set(value) {
            inventory.contents = value
        }
        get() = inventory.contents

    private val firstEmpty get() = inventory.firstEmpty()

    inline fun with(run: CustomInventory.() -> Unit): CustomInventory {
        run.invoke(this)
        return this
    }

    fun getItem(index: Int): ItemStack? {
        return if (index in 0 until inventory.size) inventory.getItem(index) else null
    }

    fun item(vararg index: Int, material: Material) {
        val item = CustomItemStack.create(material, "").toOneItemStack
        index.forEach {
            item(it, item)
        }
    }

    fun item(index: Int, item: ItemStack): Int? {
        return if (index in 0 until inventory.size) {
            inventory.setItem(index, item)
            index
        } else {
            null
        }
    }

    fun item(item: CustomItemStack): Int? {
        return item(firstEmpty, item)
    }

    fun item(index: Int, item: CustomItemStack): Int? {
        return item(index, item.toOneItemStack)
    }

    fun item(
        index: Int,
        material: Material,
        display: String,
        lore: Collection<String>,
        amount: Int = 1,
        shine: Boolean = false
    ): Int? {
        return item(index, CustomItemStack.create(material, display, *lore.toTypedArray(), amount = amount).apply {
            if (shine) {
                addEnchant(Enchantment.DURABILITY, 0)
                addItemFlag(ItemFlag.HIDE_ENCHANTS)
            }
        })
    }

    fun item(
        index: Int,
        material: Material,
        display: String,
        vararg lore: String,
        amount: Int = 1,
        shine: Boolean = false
    ): Int? {
        return item(index, material, display, lore.toList(), amount, shine)
    }

    fun Int?.event(run: () -> Unit) {
        if (this != null) {
            events[this to null] = run
        }
    }

    fun Int?.event(clickType: ClickType, run: () -> Unit): Int? {
        if (this != null) {
            events[this to clickType] = run
        }
        return this
    }

    fun open(player: Player): CustomInventory {
        player.openInventory(inventory)
        player.menuPlayer = InventoryPlayerData(id.joinToString("-").toColor, cancel, onEvent, onClick, onClose, events)
        return this
    }
}