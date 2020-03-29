package me.syari.ss.core.config.dataType

import me.syari.ss.core.config.CustomConfig

interface ConfigDataType<T> {
    val typeName: String

    fun get(config: CustomConfig, path: String, notFoundError: Boolean): T?

    fun get(config: CustomConfig, path: String, notFoundError: Boolean, default: T): T {
        return get(config, path, notFoundError) ?: default
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