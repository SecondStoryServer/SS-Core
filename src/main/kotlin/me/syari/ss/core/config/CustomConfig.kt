package me.syari.ss.core.config

import me.syari.ss.core.Main.Companion.coreLogger
import me.syari.ss.core.config.dataType.ConfigDataType
import me.syari.ss.core.message.Message.send
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.IOException

/**
 * @param plugin コンフィグがあるプラグイン
 * @param output メッセージの出力先
 * @param fileName ファイル名
 * @param directory ファイルの親フォルダ
 * @param deleteIfEmpty 中身が存在しなければ消去する
 */
class CustomConfig(
    val plugin: JavaPlugin,
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

    /**
     * @param run コンフィグに対して実行する処理
     */
    inline fun with(run: CustomConfig.() -> Unit) {
        run.invoke(this)
    }

    /**
     * @param path コンフィグパス
     * @param typeName データ型の名前
     * @param notFoundError 存在しないデータの場合にエラーを出す default: true
     * @return [T]?
     */
    inline fun <reified T> getUnsafe(path: String, typeName: String, notFoundError: Boolean = true): T? {
        if (config.contains(path)) {
            val getValue = config.get(path)
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


    /**
     * @param path コンフィグパス
     * @param typeName データ型の名前
     * @param notFoundError 存在しないデータの場合にエラーを出す default: true
     * @return [List]<[T]>?
     */
    inline fun <reified T> getListUnsafe(path: String, typeName: String, notFoundError: Boolean = true): List<T>? {
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

    /**
     * @param path コンフィグパス
     * @param type データタイプ
     * @param notFoundError 存在しないデータの場合にエラーを出す default: true
     */
    fun <T> get(path: String, type: ConfigDataType<T>, notFoundError: Boolean = true): T? {
        return type.get(this, path, notFoundError)
    }

    /**
     * @param path コンフィグパス
     * @param type データタイプ
     * @param default デフォルト値
     * @param notFoundError 存在しないデータの場合にエラーを出す default: true
     */
    fun <T> get(path: String, type: ConfigDataType<T>, default: T, notFoundError: Boolean = true): T {
        return get(path, type, notFoundError) ?: default
    }

    /**
     * @param path コンフィグパス
     * @param value 上書きする値
     * @param save 上書き後に保存する default: false
     */
    fun set(path: String, value: Any?, save: Boolean = false) {
        config.set(path, value)
        if (save) save()
    }

    /**
     * 存在するコンフィグパスかを取得する
     * @param path コンフィグパス
     */
    fun contains(path: String) = config.contains(path)

    /**
     * コンフィグセクションを取得する
     * @param path コンフィグパス
     * @param notFoundError 存在しないデータの場合にエラーを出す default: true
     */
    fun section(path: String, notFoundError: Boolean = true): Set<String>? {
        val section = config.getConfigurationSection(path)?.getKeys(false)
        return if (section != null) {
            section
        } else {
            if (notFoundError) notFoundError(path)
            null
        }
    }

    /**
     * ファイルの名前を変更します
     * @param newName 新しい名前
     */
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

    /**
     * ファイルの変更を保存します
     */
    fun save() {
        config.save(file)
        if (deleteIfEmpty && file.length() == 0L) {
            delete()
        }
    }

    /**
     * ファイルを削除します
     */
    fun delete() {
        file.delete()
        coreLogger.info("$filePath の削除に成功しました")
    }

    /**
     * エラーを出力します
     * ```
     * Format: "&6[$filePath|$path] &c$message"
     * ```
     * @param path コンフィグパス
     * @param message 本文
     */
    fun sendError(path: String, message: String) {
        output.send("&6[$filePath|$path] &c$message")
    }

    /**
     * ```
     * Format: "$thing が null です"
     * ```
     * @param path コンフィグパス
     * @param thing データ名
     */
    fun nullError(path: String, thing: String) {
        sendError(path, "$thing が null です")
    }

    /**
     * ```
     * Format: "フォーマットを間違っています"
     * ```
     * @param path コンフィグパス
     */
    fun formatMismatchError(path: String) {
        sendError(path, "フォーマットを間違っています")
    }

    /**
     * ```
     * Format: "データタイプが $typeName ではありませんでした"
     * ```
     * @param path コンフィグパス
     * @param typeName データタイプ
     */
    fun typeMismatchError(path: String, typeName: String) {
        sendError(path, "データタイプが $typeName ではありませんでした")
    }

    /**
     * ```
     * Format: "見つかりませんでした"
     * ```
     * @param path コンフィグパス
     */
    fun notFoundError(path: String) {
        sendError(path, "見つかりませんでした")
    }
}