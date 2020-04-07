package me.syari.ss.core.item

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import me.syari.ss.core.Main.Companion.corePlugin
import me.syari.ss.core.code.StringEditor.toColor
import me.syari.ss.core.persistentData.CustomPersistentData
import me.syari.ss.core.persistentData.CustomPersistentDataContainer
import org.bukkit.Material
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.configuration.serialization.ConfigurationSerialization
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.plugin.java.JavaPlugin

class CustomItemStack(private val item: ItemStack, amount: Int) : CustomPersistentDataContainer,
    ConfigurationSerializable {
    var amount = amount
        set(value) {
            item.amount = value
            field = value
        }

    var type: Material
        set(value) {
            item.type = value
        }
        get() = item.type

    val hasDisplay get() = itemMeta?.hasDisplayName() ?: false

    var display: String?
        set(value) {
            editMeta {
                setDisplayName(value?.toColor)
            }
        }
        get() = itemMeta?.displayName

    val hasLore get() = itemMeta?.hasLore() ?: false

    var lore: List<String>
        set(value) {
            editMeta {
                lore = value.toColor
            }
        }
        get() = itemMeta?.lore ?: listOf()

    fun addLore(newLine: Iterable<String>) {
        this.lore = lore.toMutableList().apply {
            addAll(newLine)
        }
    }

    fun addLore(vararg newLine: String) {
        addLore(newLine.toList())
    }

    fun containsLore(text: String): Boolean {
        val colored = text.toColor
        lore.forEach { line ->
            if (line == colored) return true
        }
        return false
    }

    var durability
        set(value) {
            editMeta {
                if (this is Damageable) {
                    damage = value
                }
            }
        }
        get() = (itemMeta as? Damageable)?.damage ?: 0

    var unbreakable: Boolean
        set(value) {
            editMeta {
                isUnbreakable = value
            }
        }
        get() = itemMeta?.isUnbreakable ?: false

    val hasItemMeta get() = item.hasItemMeta()

    private var itemMeta: ItemMeta?
        set(value) {
            item.itemMeta = value
        }
        get() = item.itemMeta

    private inline fun editMeta(run: ItemMeta.() -> Unit) {
        val meta = itemMeta ?: corePlugin.server.itemFactory.getItemMeta(type) ?: return
        run.invoke(meta)
        itemMeta = meta
    }

    fun hasItemFlag(flag: ItemFlag): Boolean {
        return itemMeta?.hasItemFlag(flag) == true
    }

    fun addItemFlag(vararg flag: ItemFlag) {
        editMeta {
            addItemFlags(*flag)
        }
    }

    fun removeItemFlag(vararg flag: ItemFlag) {
        editMeta {
            removeItemFlags(*flag)
        }
    }

    fun hasEnchant(enchant: Enchantment): Boolean {
        return itemMeta?.hasEnchant(enchant) == true
    }

    fun addEnchant(enchant: Enchantment, level: Int) {
        editMeta {
            addEnchant(enchant, level, true)
        }
    }

    fun removeEnchant(enchant: Enchantment) {
        editMeta {
            removeEnchant(enchant)
        }
    }

    val toItemStack: List<ItemStack>
        get() {
            val map = mutableListOf<ItemStack>()
            val stackNumber = amount / 64
            if (0 < stackNumber) {
                val stackItem = item.asQuantity(64)
                for (i in 0 until stackNumber) {
                    map.add(stackItem)
                }
            }
            val modNumber = amount % 64
            if (modNumber != 0) {
                val modItem = item.asQuantity(modNumber)
                modItem.amount = modNumber
                map.add(modItem)
            }
            return map
        }

    val toOneItemStack: ItemStack
        get() {
            return item.clone().apply { amount = if (64 < amount) 64 else amount }
        }

    fun isSimilar(customItem: CustomItemStack) = isSimilar(customItem.toOneItemStack)

    fun isSimilar(item: ItemStack) = toOneItemStack.isSimilar(item)

    override fun <E> editPersistentData(plugin: JavaPlugin, run: CustomPersistentData.() -> E): E? {
        var result: E? = null
        editMeta {
            result = run.invoke(
                CustomPersistentData(
                    plugin,
                    persistentDataContainer
                )
            )
        }
        return result
    }

    override fun getPersistentData(plugin: JavaPlugin): CustomPersistentData? {
        return itemMeta?.persistentDataContainer?.let { CustomPersistentData(plugin, it) }
    }

    fun clone() = CustomItemStack(item.clone(), amount)

    fun clone(run: CustomItemStack.() -> Unit) = clone().apply { run.invoke(this) }

    override fun serialize(): MutableMap<String, Any> {
        return LinkedHashMap<String, Any>().also { result ->
            result["type"] = type.name
            if (amount != 1) {
                result["amount"] = amount
            }
            val meta = itemMeta
            if (meta != null && !corePlugin.server.itemFactory.equals(meta, null)) {
                result["meta"] = meta.serialize()
            }
        }
    }

    fun toJson(): String {
        return Gson().toJson(serialize())
    }

    companion object {
        fun create(item: ItemStack?, amount: Int? = null): CustomItemStack {
            val data = if (item != null) {
                item to (amount ?: item.amount)
            } else {
                ItemStack(Material.AIR) to 0
            }
            return CustomItemStack(data.first, data.second)
        }

        fun create(material: Material?, amount: Int? = null): CustomItemStack {
            return create(material?.let { ItemStack(it) }, amount)
        }

        fun create(
            material: Material,
            display: String?,
            lore: List<String>,
            durability: Int = 0,
            amount: Int = 1
        ): CustomItemStack {
            return create(material, amount).apply {
                this.display = display
                this.lore = lore
                this.durability = durability
            }
        }

        fun create(
            material: Material,
            display: String?,
            vararg lore: String,
            durability: Int = 0,
            amount: Int = 1
        ): CustomItemStack {
            return create(material, display, lore.toList(), durability, amount)
        }

        fun fromNullable(item: ItemStack?, amount: Int = 1): CustomItemStack? {
            return if (item != null) CustomItemStack(item, amount) else null
        }

        fun fromJson(json: String): CustomItemStack {
            val map: Map<String, Any> = Gson().fromJson(json, object : TypeToken<Map<String, Any>>() {}.type)
            return fromMap(map)
        }

        private fun fromMap(args: Map<String, Any>): CustomItemStack {
            val item = ItemStack(
                Material.getMaterial(args["type"] as String) ?: Material.STONE,
                1
            )

            val amount = if (args.containsKey("amount")) {
                (args["amount"] as Number).toInt()
            } else {
                1
            }

            return CustomItemStack(item, amount).apply {
                if (args.containsKey("meta")) {
                    @Suppress("UNCHECKED_CAST")
                    val itemMetaMap = args["meta"] as MutableMap<String, Any>
                    itemMetaMap["=="] = "ItemMeta"
                    itemMeta = ConfigurationSerialization.deserializeObject(itemMetaMap) as ItemMeta
                }
            }
        }
    }
}