package me.syari.ss.core.config

import me.syari.ss.core.Main.Companion.coreLogger
import me.syari.ss.core.message.Message.send
import org.bukkit.Bukkit.getWorld
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.IOException
import java.util.*

class CustomConfig(
    plugin: JavaPlugin,
    private val output: CommandSender,
    fileName: String,
    private val directory: File,
    deleteIfEmpty: Boolean
) {
    private var file = File(directory, fileName)
    private val config: YamlConfiguration
    private val path: String

    init {
        config = YamlConfiguration.loadConfiguration(file)
        path = file.path.substringAfter(plugin.dataFolder.path).substring(1)
        if (!file.exists()) {
            try {
                file.createNewFile()
                coreLogger.info("$path の作成に成功しました")
            } catch (ex: IOException) {
                coreLogger.error("$path の作成に失敗しました")
            }
        } else if (file.length() == 0L && deleteIfEmpty) {
            coreLogger.warn("$path は中身が存在しないので削除されます")
            delete()
        }
    }

    inline fun with(run: CustomConfig.() -> Unit) {
        run.invoke(this)
    }

    fun get(path: String) = config.get(path)

    private inline fun <reified T> get(path: String, typeName: String, notFoundError: Boolean): T? {
        return if (config.contains(path)) {
            val getValue = get(path)
            if (getValue is T) {
                getValue
            } else {
                typeMismatchError<T>(path, typeName)
            }
        } else {
            notFoundError(path, notFoundError)
        }
    }

    private inline fun <reified T> getList(path: String, typeName: String, notFoundError: Boolean): List<T>? {
        return mutableListOf<T>().apply {
            get<List<*>>(path, "List", notFoundError)?.forEachIndexed { index, each ->
                if (each is T) {
                    add(each)
                } else {
                    typeMismatchInListError(path, index, typeName)
                }
            }
        }
    }

    private fun <T> getFromString(path: String, typeName: String, notFoundError: Boolean, run: (String) -> T): T? {
        val getValue = get<String>(path, typeName, notFoundError) ?: return null
        return run.invoke(getValue)
    }

    private fun <T> getListFromStringList(
        path: String,
        typeName: String,
        notFoundError: Boolean,
        run: (String) -> T?
    ): List<T>? {
        return mutableListOf<T>().apply {
            get<List<*>>(path, "List", notFoundError)?.forEachIndexed { index, each ->
                if (each is String) {
                    run.invoke(each)?.let { add(it) }
                } else {
                    typeMismatchInListError(path, index, typeName)
                }
            }
        }
    }

    private fun getNumber(path: String, typeName: String, notFoundError: Boolean): Number? {
        return get(path, typeName, notFoundError)
    }

    fun getInt(path: String, notFoundError: Boolean = true): Int? {
        return getNumber(path, "Int", notFoundError)?.toInt()
    }

    fun getInt(path: String, default: Int, notFoundError: Boolean = true): Int {
        return getInt(path, notFoundError) ?: default
    }

    fun getDouble(path: String, notFoundError: Boolean = true): Double? {
        return getNumber(path, "Double", notFoundError)?.toDouble()
    }

    fun getDouble(path: String, default: Double, notFoundError: Boolean = true): Double {
        return getDouble(path, notFoundError) ?: default
    }

    private fun getLong(path: String, notFoundError: Boolean = true): Long? {
        return getNumber(path, "Long", notFoundError)?.toLong()
    }

    fun getLong(path: String, default: Long, notFoundError: Boolean = true): Long {
        return getLong(path, notFoundError) ?: default
    }

    fun getString(path: String, notFoundError: Boolean = true): String? {
        return get(path, "String", notFoundError)
    }

    fun getString(path: String, default: String, notFoundError: Boolean = true): String {
        return getString(path, notFoundError) ?: default
    }

    fun getStringList(path: String, notFoundError: Boolean = true): List<String>? {
        return getList(path, "String", notFoundError)
    }

    fun getStringList(path: String, default: Collection<String>, notFoundError: Boolean = true): List<String> {
        return getStringList(path, notFoundError) ?: default.toList()
    }

    private fun getLocation(rawText: String): Location? {
        val split = rawText.split(",\\s*".toRegex())
        when (val size = split.size) {
            4, 6 -> {
                val world = getWorld(split[0]) ?: return nullError<Location>(path, "World(${split[0]})")
                val x = split[1].toDoubleOrNull() ?: return typeMismatchError<Location>(path, "Double")
                val y = split[2].toDoubleOrNull() ?: return typeMismatchError<Location>(path, "Double")
                val z = split[3].toDoubleOrNull() ?: return typeMismatchError<Location>(path, "Double")
                if (size == 4) return Location(world, x, y, z)
                val yaw = split[4].toFloatOrNull() ?: return typeMismatchError<Location>(path, "Float")
                val pitch = split[5].toFloatOrNull() ?: return typeMismatchError<Location>(path, "Float")
                return Location(world, x, y, z, yaw, pitch)
            }
            else -> return formatMismatchError(path)
        }
    }

    fun getLocation(path: String, notFoundError: Boolean = true): Location? {
        return getFromString(path, "String(Location)", notFoundError) { getLocation(it) }
    }

    fun getLocationList(path: String, notFoundError: Boolean = true): List<Location>? {
        return getListFromStringList(path, "String(Location)", notFoundError) { getLocation(it) }
    }

    fun getLocationList(path: String, default: List<Location>, notFoundError: Boolean = true): List<Location> {
        return getLocationList(path, notFoundError) ?: default
    }

    private fun getBoolean(path: String, notFoundError: Boolean = true): Boolean? {
        return get(path, "Boolean", notFoundError)
    }

    fun getBoolean(path: String, default: Boolean, notFoundError: Boolean = true): Boolean {
        return getBoolean(path, notFoundError) ?: default
    }

    fun getItemStack(path: String, notFoundError: Boolean = true): ItemStack? {
        return get(path, "ItemStack", notFoundError)
    }

    fun getDate(path: String, notFoundError: Boolean = true): Date? {
        return get(path, "Date", notFoundError)
    }

    private fun getColor(path: String, notFoundError: Boolean = true): Color? {
        return get(path, "Color", notFoundError)
    }

    fun getColor(path: String, default: Color, notFoundError: Boolean = true): Color {
        return getColor(path, notFoundError) ?: default
    }

    fun contains(path: String) = config.contains(path)

    fun section(
        path: String, notFoundError: Boolean = true
    ) = config.getConfigurationSection(path)?.getKeys(false) ?: notFoundError<Set<String>>(path, notFoundError)

    fun set(path: String, value: Any?, save: Boolean = true) {
        config.set(path, value)
        if (save) save()
    }

    fun rename(newName: String): Boolean {
        if (file.list()?.contains(newName) != false) return false
        return try {
            file.renameTo(File(directory, newName))
            true
        } catch (ex: SecurityException) {
            false
        } catch (ex: NullPointerException) {
            false
        }
    }

    fun save() {
        config.save(file)
        if (file.length() == 0L) {
            delete()
        }
    }

    fun delete() {
        file.delete()
        coreLogger.info("$path の削除に成功しました")
    }

    private fun sendError(ymlPath: String, message: String) {
        output.send("&b[$path|$ymlPath] &c$message")
    }

    private fun <T> notFoundError(path: String, sendNotFound: Boolean): T? {
        if (sendNotFound) sendError(path, "見つかりませんでした")
        return null
    }

    private fun <T> nullError(path: String, thing: String): T? {
        sendError(path, "$thing が null でした")
        return null
    }

    private fun <T> formatMismatchError(path: String): T? {
        sendError(path, "フォーマットを間違っています")
        return null
    }

    private fun <T> typeMismatchError(path: String, typeName: String): T? {
        sendError(path, "データタイプが $typeName ではありませんでした")
        return null
    }

    private fun typeMismatchInListError(path: String, index: Int, typeName: String) {
        sendError(path, "${index + 1} のデータタイプが $typeName ではありませんでした")
    }
}