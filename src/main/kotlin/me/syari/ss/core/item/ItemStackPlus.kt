package me.syari.ss.core.item

import org.bukkit.entity.HumanEntity
import org.bukkit.inventory.ItemStack

object ItemStackPlus {
    fun HumanEntity.giveOrDrop(items: Iterable<ItemStack?>) {
        val location = location
        items.forEach { item ->
            if (item == null) return@forEach
            if (inventory.firstEmpty() in 0 until 36) {
                inventory.addItem(item.clone())
            } else {
                location.world.dropItem(location, item.clone())
            }
        }
    }

    fun HumanEntity.giveOrDrop(vararg items: CustomItemStack) {
        giveOrDrop(items.flatMap { it.toItemStack })
    }
}