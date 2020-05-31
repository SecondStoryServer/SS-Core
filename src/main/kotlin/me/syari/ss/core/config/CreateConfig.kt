package me.syari.ss.core.config

import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

object CreateConfig {
    /**
     * コンフィグをロードします
     * @param plugin ロードするプラグイン
     * @param output メッセージの出力先
     * @param fileName ファイル名 最後は必ず.yml
     * @param deleteIfEmpty 中身が存在しなければ消去する default: true
     * @return [CustomConfig]
     */
    fun config(
        plugin: JavaPlugin,
        output: CommandSender,
        fileName: String,
        deleteIfEmpty: Boolean = true,
        default: Map<String, Any> = emptyMap()
    ): CustomConfig {
        var directory = plugin.dataFolder
        if (!directory.exists()) directory.mkdir()
        fileName.split("/".toRegex()).forEach { file ->
            if (file.endsWith(".yml")) {
                return CustomConfig(plugin, output, file, directory, deleteIfEmpty, default)
            } else {
                directory = File(directory, file)
                if (!directory.exists()) {
                    directory.mkdir()
                }
            }
        }
        throw IllegalArgumentException("ファイル名の最後が .yml ではありません ($fileName)")
    }

    /**
     * コンフィグをロードします
     * @param plugin ロードするプラグイン
     * @param output メッセージの出力先
     * @param fileName ファイル名 最後は必ず.yml
     * @param deleteIfEmpty 中身が存在しなければ消去する default: true
     * @param run コンフィグに対して実行する処理
     * @return [CustomConfig]
     */
    fun config(
        plugin: JavaPlugin,
        output: CommandSender,
        fileName: String,
        deleteIfEmpty: Boolean = true,
        default: Map<String, Any> = emptyMap(),
        run: CustomConfig.() -> Unit
    ): CustomConfig {
        return config(plugin, output, fileName, deleteIfEmpty, default).apply(run)
    }

    /**
     * フォルダ内のコンフィグを全てロードします
     * @param plugin ロードするプラグイン
     * @param output メッセージの出力先
     * @param directoryName フォルダ名
     * @param deleteIfEmpty 中身が存在しなければ消去する default: true
     * @return [Map]<[String], [CustomConfig]>
     */
    fun configDir(
        plugin: JavaPlugin,
        output: CommandSender,
        directoryName: String,
        deleteIfEmpty: Boolean = true
    ): Map<String, CustomConfig> {
        var directory = plugin.dataFolder
        if (!directory.exists()) directory.mkdir()
        directoryName.split("/".toRegex()).forEach { subDirectory ->
            directory = File(directory, subDirectory)
            if (!directory.exists()) directory.mkdir()
        }
        return mutableMapOf<String, CustomConfig>().apply {
            directory.list()?.forEach { fileName ->
                if (fileName.endsWith(".yml")) {
                    this[fileName] = CustomConfig(plugin, output, fileName, directory, deleteIfEmpty)
                }
            }
        }
    }

    /**
     * フォルダ内のコンフィグを全てロードします
     * @param plugin ロードするプラグイン
     * @param output メッセージの出力先
     * @param directoryName フォルダ名
     * @param deleteIfEmpty 中身が存在しなければ消去する default: true
     * @param run コンフィグに対して実行する処理
     * @return [Map]<[String], [CustomConfig]>
     */
    fun configDir(
        plugin: JavaPlugin,
        output: CommandSender,
        directoryName: String,
        deleteIfEmpty: Boolean = true,
        run: CustomConfig.() -> Unit
    ) {
        configDir(plugin, output, directoryName, deleteIfEmpty).values.apply {
            forEach { config ->
                run.invoke(config)
            }
        }
    }

    /**
     * 存在するコンフィグファイルかを取得する
     * @param plugin 調べるプラグイン
     * @param fileName 対象のファイル名 最後は必ず.yml
     * @return [Boolean]
     */
    fun contains(plugin: JavaPlugin, fileName: String): Boolean {
        var directory = plugin.dataFolder
        if (!directory.exists()) return false
        fileName.split("/".toRegex()).forEach { file ->
            if (file.endsWith(".yml")) {
                directory.list()?.forEach { directoryContent ->
                    if (file == directoryContent) {
                        return true
                    }
                }
            } else {
                directory = File(directory, file)
                if (!directory.exists()) {
                    return false
                }
            }
        }
        return false
    }
}