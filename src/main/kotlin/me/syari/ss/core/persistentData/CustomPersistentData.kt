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
}