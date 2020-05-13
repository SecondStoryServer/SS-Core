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

    /**
     * クリックイベントキャンセル
     */
    var cancel = true

    /**
     * インベントリイベント
     */
    var onEvent: ((InventoryEvent) -> Unit)? = null

    /**
     * クリックイベント
     */
    var onClick: ((InventoryClickEvent) -> Unit)? = null

    /**
     * クローズイベント
     */
    var onClose: ((InventoryCloseEvent) -> Unit)? = null

    /**
     * アイテム
     */
    var contents: Array<ItemStack>
        set(value) {
            inventory.contents = value
        }
        get() = inventory.contents

    /**
     * @param index 取得するアイテムのインデックス
     */
    fun getItem(index: Int): ItemStack? {
        return if (index in 0 until inventory.size) inventory.getItem(index) else null
    }

    /**
     * 見た目のみのアイテムを配置します
     * @param index アイテムの場所
     * @param material アイテムタイプ
     */
    fun item(index: Iterable<Int>, material: Material) {
        val item = CustomItemStack.create(material, "").toOneItemStack
        index.forEach {
            item(it, item)
        }
    }

    /**
     * 見た目のみのアイテムを配置します
     * @param index アイテムの場所
     * @param material アイテムタイプ
     */
    fun item(vararg index: Int, material: Material) {
        item(index.toList(), material)
    }

    /**
     * 見た目のみのアイテムを配置します
     * @param index アイテムの場所
     * @param material アイテムタイプ
     */
    fun item(index: IntRange, material: Material) {
        item(index.toList(), material)
    }

    /**
     * @param index アイテムの場所
     * @param item アイテム
     * @return [ClickEvent]
     */
    fun item(index: Int, item: ItemStack): ClickEvent {
        return if (index in 0 until inventory.size) {
            inventory.setItem(index, item)
            ClickEvent(this, listOf(index))
        } else {
            ClickEvent(this, listOf())
        }
    }

    /**
     * @param index アイテムの場所
     * @param item アイテム
     * @return [ClickEvent]
     */
    fun item(index: Int, item: CustomItemStack): ClickEvent {
        return item(index, item.toOneItemStack)
    }

    /**
     * ```
     * item(inventory.firstEmpty(), item)
     * ```
     * @param item アイテム
     * @return [ClickEvent]
     */
    fun item(item: CustomItemStack): ClickEvent {
        return item(inventory.firstEmpty(), item)
    }

    /**
     * @param index アイテムの場所
     * @param material アイテムタイプ
     * @param display アイテム名
     * @param lore アイテムの説明文
     * @param amount アイテムの数
     * @param shine エンチャントを付与する default: false
     * @return [ClickEvent]
     */
    fun item(
        index: Int,
        material: Material,
        display: String,
        lore: Collection<String>,
        amount: Int = 1,
        shine: Boolean = false
    ): ClickEvent {
        return item(listOf(index), material, display, lore.toList(), amount, shine)
    }

    /**
     * @param index アイテムの場所
     * @param material アイテムタイプ
     * @param display アイテム名
     * @param lore アイテムの説明文
     * @param amount アイテムの数
     * @param shine エンチャントを付与する default: false
     * @return [ClickEvent]
     */
    fun item(
        index: Iterable<Int>,
        material: Material,
        display: String,
        lore: Collection<String>,
        amount: Int = 1,
        shine: Boolean = false
    ): ClickEvent {
        return ClickEvent(this, index.map {
            item(it, CustomItemStack.create(material, display, *lore.toTypedArray(), amount = amount).apply {
                if (shine) {
                    addEnchant(Enchantment.DURABILITY, 0)
                    addItemFlag(ItemFlag.HIDE_ENCHANTS)
                }
            })
        }.flatMap { it.slot })
    }

    /**
     * @param index アイテムの場所
     * @param material アイテムタイプ
     * @param display アイテム名
     * @param lore アイテムの説明文
     * @param amount アイテムの数
     * @param shine エンチャントを付与する default: false
     * @return [ClickEvent]
     */
    fun item(
        index: Int,
        material: Material,
        display: String,
        vararg lore: String,
        amount: Int = 1,
        shine: Boolean = false
    ): ClickEvent {
        return item(listOf(index), material, display, lore.toList(), amount, shine)
    }

    /**
     * @param index アイテムの場所
     * @param material アイテムタイプ
     * @param display アイテム名
     * @param lore アイテムの説明文
     * @param amount アイテムの数
     * @param shine エンチャントを付与する default: false
     * @return [ClickEvent]
     */
    fun item(
        index: Iterable<Int>,
        material: Material,
        display: String,
        vararg lore: String,
        amount: Int = 1,
        shine: Boolean = false
    ): ClickEvent {
        return item(index, material, display, lore.toList(), amount, shine)
    }

    /**
     * アイテム単位でクリックイベントを設定します
     */
    data class ClickEvent(val inventory: CustomInventory, val slot: List<Int>) {
        /**
         * @param clickType クリックタイプ
         * @param run クリックタイプが一致した時に実行する処理
         * @return [ClickEvent]
         */
        fun event(vararg clickType: ClickType, run: () -> Unit): ClickEvent {
            clickType.forEach { type ->
                addEvent(type, run)
            }
            return this
        }

        /**
         * @param run アイテムがクリックされた時に実行する処理
         * @return [ClickEvent]
         */
        fun event(run: () -> Unit): ClickEvent {
            addEvent(null, run)
            return this
        }

        private fun addEvent(clickType: ClickType?, run: () -> Unit) {
            slot.forEach {
                inventory.events[it to clickType] = run
            }
        }
    }

    /**
     * プレイヤーにインベントリを開かせます
     * @param player 対象プレイヤー
     */
    fun open(player: Player): CustomInventory {
        player.openInventory(inventory)
        player.menuPlayer = InventoryPlayerData(id.joinToString("-").toColor, cancel, onEvent, onClick, onClose, events)
        return this
    }
}