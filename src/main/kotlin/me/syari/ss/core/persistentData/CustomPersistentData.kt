package me.syari.ss.core.persistentData

import org.bukkit.NamespacedKey
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin

class CustomPersistentData(
    private val plugin: JavaPlugin,
    private val persistentDataContainer: PersistentDataContainer
) {
    private val String.asNamespacedKey get() = NamespacedKey(plugin, this)

    private fun <T, Z> has(key: String, type: PersistentDataType<T, Z>): Boolean {
        return persistentDataContainer.has(key.asNamespacedKey, type)
    }

    private fun <T, Z> get(key: String, type: PersistentDataType<T, Z>): Z? {
        return if (has(key, type)) {
            persistentDataContainer.get(key.asNamespacedKey, type)
        } else {
            null
        }
    }

    fun getString(key: String): String? {
        return get(key, PersistentDataType.STRING)
    }

    fun getStringList(key: String): List<String>? {
        return getString(key)?.split("§, ")
    }

    fun getBoolean(key: String, default: Boolean): Boolean {
        return getBoolean(key) ?: default
    }

    fun getBoolean(key: String): Boolean? {
        return get(key, PersistentDataType.BYTE) != 0.toByte()
    }

    fun getInt(key: String): Int? {
        return get(key, PersistentDataType.INTEGER)
    }

    private fun <T, Z> set(key: String, type: PersistentDataType<T, Z>, value: Z?) {
        if (value != null) {
            persistentDataContainer.set(key.asNamespacedKey, type, value)
        } else {
            removeKey(key)
        }
    }

    fun setString(key: String, value: String?) {
        set(key, PersistentDataType.STRING, value)
    }

    fun setStringList(key: String, value: List<String>?) {
        set(key, PersistentDataType.STRING, value?.joinToString("§, "))
    }

    fun setBoolean(key: String, value: Boolean?) {
        set(
            key, PersistentDataType.BYTE, when (value) {
                true -> 1.toByte()
                false -> 0.toByte()
                null -> null
            }
        )
    }

    fun setInt(key: String, value: Int?) {
        set(key, PersistentDataType.INTEGER, value)
    }

    fun removeKey(key: String) {
        persistentDataContainer.remove(key.asNamespacedKey)
    }
}