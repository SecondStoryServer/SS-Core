package me.syari.ss.core.config.dataType

import me.syari.ss.core.config.CustomConfig
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

object ConfigPotionDataType : ConfigDataType<List<PotionEffect>> {
    override val typeName = "List<PotionEffect>"

    override fun get(config: CustomConfig, path: String, notFoundError: Boolean): List<PotionEffect>? {
        val getList = config.get(path, ConfigDataType.STRINGLIST, notFoundError) ?: return null
        return mutableListOf<PotionEffect>().apply {
            getList.forEachIndexed { index, line ->
                val split = line.split("-")
                if (split.size < 3) {
                    config.formatMismatchError("$path:$index")
                    return@forEachIndexed
                }
                val type = PotionEffectType.getByName(split[0])
                if (type == null) {
                    config.nullError("$path:$index", "Potion(${split[0]})")
                    return@forEachIndexed
                }
                val duration = split[1].toIntOrNull()
                if (duration == null) {
                    config.typeMismatchError("$path:$index", "Int(${split[1]})")
                    return@forEachIndexed
                }
                val level = split[2].toIntOrNull()
                if (level == null) {
                    config.typeMismatchError("$path:$index", "Int(${split[2]})")
                    return@forEachIndexed
                }
                val particle = if (split.size < 4) {
                    split[3].toBoolean()
                } else {
                    true
                }
                PotionEffect(type, duration, level, true, particle)
            }
        }
    }
}