package me.syari.ss.core.persistentData

import me.syari.ss.core.persistentData.customType.PersistentDataTypeBoolean
import me.syari.ss.core.persistentData.customType.PersistentDataTypeCustomItemStack
import me.syari.ss.core.persistentData.customType.PersistentDataTypeUUID
import org.bukkit.NamespacedKey
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin

class CustomPersistentData(
    private val plugin: JavaPlugin,
    private val persistentDataContainer: PersistentDataContainer
) {
    private val String.asNamespacedKey get() = NamespacedKey(plugin, this)

    fun <T, Z> has(key: String, type: PersistentDataType<T, Z>): Boolean {
        return persistentDataContainer.has(key.asNamespacedKey, type)
    }

    fun <T, Z> get(key: String, type: PersistentDataType<T, Z>): Z? {
        return if (has(key, type)) {
            persistentDataContainer.get(key.asNamespacedKey, type)
        } else {
            null
        }
    }

    fun <T, Z> get(key: String, type: PersistentDataType<T, Z>, default: Z): Z {
        return get(key, type) ?: default
    }

    fun <T, Z> set(key: String, type: PersistentDataType<T, Z>, value: Z?) {
        if (value != null) {
            persistentDataContainer.set(key.asNamespacedKey, type, value)
        } else {
            remove(key)
        }
    }

    fun remove(key: String) {
        persistentDataContainer.remove(key.asNamespacedKey)
    }

    companion object {
        val BYTE: PersistentDataType<Byte, Byte> = PersistentDataType.BYTE
        val SHORT: PersistentDataType<Short, Short> = PersistentDataType.SHORT
        val INT: PersistentDataType<Int, Int> = PersistentDataType.INTEGER
        val LONG: PersistentDataType<Long, Long> = PersistentDataType.LONG
        val FLOAT: PersistentDataType<Float, Float> = PersistentDataType.FLOAT
        val DOUBLE: PersistentDataType<Double, Double> = PersistentDataType.DOUBLE
        val STRING: PersistentDataType<String, String> = PersistentDataType.STRING
        val UUID = PersistentDataTypeUUID
        val BOOLEAN = PersistentDataTypeBoolean
        val ITEM = PersistentDataTypeCustomItemStack
    }
}