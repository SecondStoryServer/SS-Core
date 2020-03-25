package me.syari.ss.core.config

import me.syari.ss.core.Main.Companion.coreLogger
import me.syari.ss.core.message.Message.send
import me.syari.ss.core.particle.CustomParticle
import me.syari.ss.core.particle.CustomParticleList
import me.syari.ss.core.sound.CustomSound
import me.syari.ss.core.sound.CustomSoundList
import org.bukkit.*
import org.bukkit.Bukkit.getWorld
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.io.File
import java.io.IOException
import java.util.*

class CustomConfig(
    plugin: JavaPlugin,
    private val output: CommandSender,
    val fileName: String,
    private val directory: File,
    private val deleteIfEmpty: Boolean
) {
    private var file = File(directory, fileName)
    private val config: YamlConfiguration
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
            if (config.isList(path)) {
                get<List<*>>(path, "List", notFoundError)?.forEachIndexed { index, each ->
                    if (each is T) {
                        add(each)
                    } else {
                        typeMismatchError("$path:$index", typeName)
                    }
                }
            } else {
                get<T>(path, "List", notFoundError)?.let {
                    add(it)
                }
            }
        }
    }

    private fun <T> getFromString(path: String, typeName: String, notFoundError: Boolean, run: (String) -> T?): T? {
        val getValue = get<String>(path, typeName, notFoundError) ?: return null
        return run.invoke(getValue)
    }

    private fun <T> getListFromStringList(
        path: String,
        typeName: String,
        notFoundError: Boolean,
        run: (String, Int) -> T?
    ): List<T>? {
        return mutableListOf<T>().apply {
            get<List<*>>(path, "List", notFoundError)?.forEachIndexed { index, each ->
                if (each is String) {
                    run.invoke(each, index)?.let { add(it) }
                } else {
                    typeMismatchError("$path:$index", typeName)
                }
            }
        }
    }

    fun getNumber(path: String, typeName: String, notFoundError: Boolean): Number? {
        return get(path, typeName, notFoundError)
    }

    fun getInt(path: String, notFoundError: Boolean = true): Int? {
        return getNumber(path, "Int", notFoundError)?.toInt()
    }

    fun getInt(path: String, default: Int, notFoundError: Boolean = true): Int {
        return getInt(path, notFoundError) ?: default
    }

    fun getFloat(path: String, notFoundError: Boolean = true): Float? {
        return getNumber(path, "Float", notFoundError)?.toFloat()
    }

    fun getFloat(path: String, default: Float, notFoundError: Boolean = true): Float {
        return getFloat(path, notFoundError) ?: default
    }

    fun getLong(path: String, notFoundError: Boolean = true): Long? {
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

    private fun getLocation(path: String, rawText: String): Location? {
        val split = rawText.split(",\\s*".toRegex())
        when (val size = split.size) {
            4, 6 -> {
                val world = getWorld(split[0]) ?: return nullError<Location>(path, "World(${split[0]})")
                val x = split[1].toDoubleOrNull() ?: return typeMismatchError<Location>(path, "Double(${split[1]})")
                val y = split[2].toDoubleOrNull() ?: return typeMismatchError<Location>(path, "Double(${split[2]})")
                val z = split[3].toDoubleOrNull() ?: return typeMismatchError<Location>(path, "Double(${split[3]})")
                if (size == 4) return Location(world, x, y, z)
                val yaw = split[4].toFloatOrNull() ?: return typeMismatchError<Location>(path, "Float(${split[4]})")
                val pitch = split[5].toFloatOrNull() ?: return typeMismatchError<Location>(path, "Float(${split[5]})")
                return Location(world, x, y, z, yaw, pitch)
            }
            else -> return formatMismatchError<Location>(path)
        }
    }

    fun getLocation(path: String, notFoundError: Boolean = true): Location? {
        return getFromString(path, "String(Location)", notFoundError) { getLocation(path, it) }
    }

    fun getLocationList(path: String, notFoundError: Boolean = true): List<Location>? {
        return getListFromStringList(path, "String(Location)", notFoundError) { line, index ->
            getLocation("$path:$index", line)
        }
    }

    fun getLocationList(path: String, default: List<Location>, notFoundError: Boolean = true): List<Location> {
        return getLocationList(path, notFoundError) ?: default
    }

    fun getBoolean(path: String, notFoundError: Boolean = true): Boolean? {
        return get(path, "Boolean", notFoundError)
    }

    fun getBoolean(path: String, default: Boolean, notFoundError: Boolean = true): Boolean {
        return getBoolean(path, notFoundError) ?: default
    }

    fun getDate(path: String, notFoundError: Boolean = true): Date? {
        return get(path, "Date", notFoundError)
    }

    fun getMaterial(path: String, notFoundError: Boolean = true): Material? {
        return getFromString(path, "String(Material)", notFoundError) { Material.getMaterial(it) }
    }

    fun getMaterial(path: String, default: Material, notFoundError: Boolean = true): Material {
        return getMaterial(path, notFoundError) ?: default
    }

    private fun getPotionEffect(path: String, rawText: String): PotionEffect? {
        val split = rawText.split("-")
        if (split.size < 3) return formatMismatchError<PotionEffect>(path)
        val type = PotionEffectType.getByName(split[0]) ?: return nullError<PotionEffect>(path, "Potion(${split[0]})")
        val duration = split[1].toIntOrNull() ?: return typeMismatchError<PotionEffect>(path, "Int(${split[1]})")
        val level = split[2].toIntOrNull() ?: return typeMismatchError<PotionEffect>(path, "Int(${split[2]})")
        if (split.size < 4) return PotionEffect(type, duration, level)
        val particle = split[3].toBoolean()
        return PotionEffect(type, duration, level, true, particle)
    }

    fun getPotionEffectList(path: String, notFoundError: Boolean = true): List<PotionEffect>? {
        return getListFromStringList(path, "String(Potion)", notFoundError) { line, index ->
            getPotionEffect("$path:$index", line)
        }
    }

    fun getPotionEffectList(
        path: String,
        default: List<PotionEffect>,
        notFoundError: Boolean = true
    ): List<PotionEffect> {
        return getPotionEffectList(path, notFoundError) ?: default
    }

    private fun CustomSoundList.addElement(path: String, rawText: String) {
        val split = rawText.split("-")
        when (val size = split.size) {
            2 -> {
                if (split[0].toLowerCase() == "delay") {
                    val delay = split[1].toLongOrNull() ?: return nullError(path, "Long(${split[1]})")
                    return addDelay(delay)
                }
            }
            3, 4 -> {
                val rawType = split[0].toUpperCase()
                val type =
                    Sound.values().firstOrNull { rawType == it.name } ?: return nullError(path, "Sound($rawType)")
                val volume = split[1].toFloatOrNull() ?: return nullError(path, "Float(${split[1]})")
                val pitch = split[2].toFloatOrNull() ?: return nullError(path, "Float(${split[2]})")
                val loop = if (size == 3) {
                    1
                } else {
                    split[3].toIntOrNull() ?: return nullError(path, "Int(${split[3]})")
                }
                for (i in 0 until loop) {
                    addSound(CustomSound(type, volume, pitch, SoundCategory.MASTER))
                }
            }
        }
        formatMismatchError(path)
    }

    fun getCustomSoundList(path: String, notFoundError: Boolean = true): CustomSoundList? {
        return getStringList(path, notFoundError)?.let { lines ->
            CustomSoundList().apply {
                lines.forEachIndexed { index, line ->
                    addElement("$path:$index", line)
                }
            }
        }
    }

    private fun CustomParticleList.addElement(path: String, rawText: String) {
        val split = rawText.split("-")
        when (split.size) {
            2 -> {
                if (split[0].toLowerCase() == "delay") {
                    val delay = split[1].toLongOrNull() ?: return nullError(path, "Long(${split[1]})")
                    return addDelay(delay)
                }
            }
            3, 4, 6 -> {
                val rawType = split[0].toUpperCase()
                val type =
                    Particle.values().firstOrNull { rawType == it.name } ?: return nullError(path, "Particle($rawType)")
                val lastIndex = split.lastIndex
                val speed =
                    split[lastIndex - 1].toDoubleOrNull() ?: return nullError(path, "Double(${split[lastIndex - 1]})")
                val count = split[lastIndex].toIntOrNull() ?: return nullError(path, "Int(${split[lastIndex]})")
                return addParticle(
                    when (type) {
                        Particle.ITEM_CRACK, Particle.BLOCK_CRACK, Particle.BLOCK_DUST, Particle.FALLING_DUST -> {
                            val material =
                                Material.getMaterial(split[2]) ?: return nullError(path, "Material(${split[2]})")
                            when (type) {
                                Particle.ITEM_CRACK -> CustomParticle.ItemCrack(material, count, speed)
                                Particle.BLOCK_CRACK -> CustomParticle.BlockCrack(material, count, speed)
                                Particle.BLOCK_DUST -> CustomParticle.BlockDust(material, count, speed)
                                Particle.FALLING_DUST -> CustomParticle.FallingDust(material, count, speed)
                                else -> return // Unreachable
                            }
                        }
                        Particle.REDSTONE -> {
                            val red = split[2].toIntOrNull() ?: return nullError(path, "Int(${split[2]})")
                            val green = split[3].toIntOrNull() ?: return nullError(path, "Int(${split[3]})")
                            val blue = split[4].toIntOrNull() ?: return nullError(path, "Int(${split[4]})")
                            CustomParticle.RedStone(red, blue, green, count, speed)
                        }
                        else -> CustomParticle.Normal(type, count, speed)
                    }
                )
            }
        }
        formatMismatchError(path)
    }

    fun getParticleList(path: String, notFoundError: Boolean = true): CustomParticleList? {
        return getStringList(path, notFoundError)?.let { lines ->
            CustomParticleList().apply {
                lines.forEachIndexed { index, line ->
                    addElement("$path:$index", line)
                }
            }
        }
    }

    fun contains(path: String) = config.contains(path)

    fun section(path: String, notFoundError: Boolean = true): Set<String>? {
        return config.getConfigurationSection(path)?.getKeys(false) ?: notFoundError<Set<String>>(path, notFoundError)
    }

    fun set(path: String, value: Any?, save: Boolean = false) {
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
        if (deleteIfEmpty && file.length() == 0L) {
            delete()
        }
    }

    fun delete() {
        file.delete()
        coreLogger.info("$filePath の削除に成功しました")
    }

    fun sendError(ymlPath: String, message: String) {
        output.send("&b[$filePath|$ymlPath] &c$message")
    }

    private fun <T> notFoundError(path: String, notFoundError: Boolean): T? {
        if (notFoundError) sendError(path, "見つかりませんでした")
        return null
    }

    private fun nullError(path: String, thing: String) {
        sendError(path, "$thing が null でした")
    }

    private fun <T> nullError(path: String, thing: String): T? {
        nullError(path, thing)
        return null
    }

    private fun formatMismatchError(path: String) {
        sendError(path, "フォーマットを間違っています")
    }

    private fun <T> formatMismatchError(path: String): T? {
        formatMismatchError(path)
        return null
    }

    private fun typeMismatchError(path: String, typeName: String) {
        sendError(path, "データタイプが $typeName ではありませんでした")
    }

    private fun <T> typeMismatchError(path: String, typeName: String): T? {
        typeMismatchError(path, typeName)
        return null
    }
}