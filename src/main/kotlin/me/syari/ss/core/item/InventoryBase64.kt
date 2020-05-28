package me.syari.ss.core.item

import org.bukkit.Bukkit
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

object InventoryBase64 {
    /**
     * プレイヤーインベントリを Base64 に変換します
     * @param playerInventory 対象プレイヤーインベントリ
     * @return [String]
     */
    fun toBase64(playerInventory: PlayerInventory): String {
        return toBase64(playerInventory.contents)
    }

    /**
     * アイテムを Base64 に変換します
     * @param items 対象アイテム
     * @return [String]
     */
    fun toBase64(items: Array<ItemStack>): String {
        return toBase64(items.toList())
    }

    /**
     * アイテムを Base64 に変換します
     * @param items 対象アイテム
     * @return [String]
     */
    fun toBase64(items: Collection<ItemStack>): String {
        val outputStream = ByteArrayOutputStream()
        val data = BukkitObjectOutputStream(outputStream)
        data.writeInt(items.size)
        for (item in items) {
            data.writeObject(item)
        }
        data.close()
        return Base64Coder.encodeLines(outputStream.toByteArray())
    }

    /**
     * Base64 をプレイヤーインベントリに変換します
     * @param base64 Base64 データ
     * @return [Inventory]
     */
    fun getInventoryFromBase64(base64: String): Inventory {
        return Bukkit.getServer().createInventory(null, InventoryType.PLAYER).apply {
            contents = fromBase64(base64)
        }
    }

    /**
     * Base64 をアイテムに変換します
     * @param base64 Base64 データ
     * @return [List]<[ItemStack]>
     */
    fun getItemStackFromBase64(base64: String): List<ItemStack> {
        return fromBase64(base64).filterNotNull()
    }

    private fun fromBase64(base64: String): Array<ItemStack?> {
        val inputStream = ByteArrayInputStream(Base64Coder.decodeLines(base64))
        val data = BukkitObjectInputStream(inputStream)
        val items = arrayOfNulls<ItemStack>(data.readInt())
        for (i in items.indices) {
            items[i] = data.readObject() as? ItemStack
        }
        data.close()
        return items
    }
}