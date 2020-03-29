package me.syari.ss.core.config

import me.syari.ss.core.Main.Companion.coreLogger
import me.syari.ss.core.config.dataType.*
import me.syari.ss.core.message.Message.send
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.IOException

class CustomConfig(
    plugin: JavaPlugin,
    private val output: CommandSender,
    val fileName: String,
    private val directory: File,
    private val deleteIfEmpty: Boolean
) {
    private var file = File(directory, fileName)
    val config: YamlConfiguration
    private val filePath: String

    init {
        config = YamlConfiguration.loadConfiguration(file)
        filePath = file.path.substringAfter(plugin.dataFolder.path).substring(1)
        if (!file.exists()) {
            try {
                file.createNewFile()
                coreLogger.info("$filePath の作成に成功しました")
            } catch (ex: IOException) {
                coreLogger.error("$filePath の作成に失敗しました")
            }
        } else if (file.length() == 0L && deleteIfEmpty) {
            coreLogger.warn("$filePath は中身が存在しないので削除されます")
            delete()
        }
    }

    inline fun with(run: CustomConfig.() -> Unit) {
        run.invoke(this)
    }

    fun get(path: String) = config.get(path)

    inline fun <reified T> getUnsafe(path: String, typeName: String, notFoundError: Boolean): T? {
        if (config.contains(path)) {
            val getValue = get(path)
            if (getValue is T) {
                return getValue
            } else {
                typeMismatchError(path, typeName)
            }
        } else if (notFoundError) {
            notFoundError(path)
        }
        return null
    }

    inline fun <reified T> getListUnsafe(path: String, typeName: String, notFoundError: Boolean): List<T>? {
        return mutableListOf<T>().apply {
            if (config.isList(path)) {
                getUnsafe<List<*>>(path, "List<$typeName>", notFoundError)?.forEachIndexed { index, each ->
                    if (each is T) {
                        add(each)
                    } else {
                        typeMismatchError("$path:$index", typeName)
                    }
                }
            } else {
                getUnsafe<T>(path, typeName, notFoundError)?.let {
                    add(it)
                }
            }
        }
    }

    fun <T> get(path: String, type: ConfigDataType<T>, notFoundError: Boolean): T? {
        return type.get(this, path, notFoundError)
    }

    fun <T> get(path: String, type: ConfigDataType<T>, notFoundError: Boolean, default: T): T {
        return get(path, type, notFoundError) ?: default
    }

    fun set(path: String, value: Any?, save: Boolean = false) {
        config.set(path, value)
        if (save) save()
    }

    fun contains(path: String) = config.contains(path)

    fun section(path: String, notFoundError: Boolean = true): Set<String>? {
        val section = config.getConfigurationSection(path)?.getKeys(false)
        return if (section != null) {
            section
        } else {
            if (notFoundError) notFoundError(path)
            null
        }
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
        if (deleteIfEmpty && file.length() == 0L) {
            delete()
        }
    }

    fun delete() {
        file.delete()
        coreLogger.info("$filePath の削除に成功しました")
    }

    fun sendError(ymlPath: String, message: String) {
        output.send("&6[$filePath|$ymlPath] &c$message")
    }

    fun nullError(path: String, thing: String) {
        sendError(path, "$thing がnullです")
    }

    fun formatMismatchError(path: String) {
        sendError(path, "フォーマットを間違っています")
    }

    fun typeMismatchError(path: String, typeName: String) {
        sendError(path, "データタイプが $typeName ではありませんでした")
    }

    fun notFoundError(path: String) {
        sendError(path, "$path が見つかりませんでした")
    }

    companion object {
        val NUMBER = ConfigNumberDataType
        val INT = ConfigIntDataType
        val LONG = ConfigLongDataType
        val FLOAT = ConfigFloatDataType
        val STRING = ConfigStringDataType
        val STRINGLIST = ConfigStringListDataType
        val BOOLEAN = ConfigBooleanDataType
        val DATE = ConfigDateDataType
        val LOCATION = ConfigLocationDataType
        val MATERIAL = ConfigMaterialDataType
        val PARTICLE = ConfigParticleDataType
        val POTION = ConfigPotionDataType
        val SOUND = ConfigSoundDataType
    }
}