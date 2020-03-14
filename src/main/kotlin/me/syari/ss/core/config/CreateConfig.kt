package me.syari.ss.core.config

import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

object CreateConfig {
    fun config(
        plugin: JavaPlugin,
        output: CommandSender,
        fileName: String,
        deleteIfEmpty: Boolean = true
    ): CustomConfig {
        var directory = plugin.dataFolder
        if (!directory.exists()) directory.mkdir()
        fileName.split("/".toRegex()).forEach { file ->
            if (file.endsWith(".yml")) {
                return CustomConfig(plugin, output, file, directory, deleteIfEmpty)
            } else {
                directory = File(directory, file)
                if (!directory.exists()) {
                    directory.mkdir()
                }
            }
        }
        throw Exception("ファイル名の最後が .yml ではありません ($fileName)")
    }

    fun config(
        plugin: JavaPlugin,
        output: CommandSender,
        fileName: String,
        deleteIfEmpty: Boolean = true,
        run: CustomConfig.() -> Unit
    ): CustomConfig {
        return config(plugin, output, fileName, deleteIfEmpty).apply { run.invoke(this) }
    }

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
            directory.list()?.forEach { file ->
                if (file.endsWith(".yml")) {
                    this[file] = CustomConfig(plugin, output, file, directory, deleteIfEmpty)
                }
            }
        }
    }

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