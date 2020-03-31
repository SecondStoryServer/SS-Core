package me.syari.ss.core.item

import org.bukkit.entity.HumanEntity
import org.bukkit.inventory.ItemStack

object ItemStackPlus {
    fun HumanEntity.give(item: ItemStack) {
        inventory.addItem(item)
    }

    fun HumanEntity.give(items: Iterable<ItemStack?>) {
        items.forEach {
            if (it != null) give(it)
        }
    }

    fun HumanEntity.give(item: CustomItemStack) {
        give(item.toItemStack)
    }

    fun HumanEntity.giveOrDrop(item: ItemStack) {
        if (inventory.firstEmpty() in 0 until 36) {
            give(item)
        } else {
            location.world.dropItem(location, item)
        }
    }

    fun HumanEntity.giveOrDrop(items: Iterable<ItemStack?>) {
        items.forEach { item ->
            if (item != null) giveOrDrop(item)
        }
    }

    fun HumanEntity.giveOrDrop(item: CustomItemStack) {
        giveOrDrop(item.toItemStack)
    }
}