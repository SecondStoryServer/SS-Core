package me.syari.ss.core.config.dataType

import me.syari.ss.core.config.CustomConfig

object ConfigIntDataType : ConfigDataType<Int> {
    override val typeName = "Int"

    override fun get(config: CustomConfig, path: String, notFoundError: Boolean): Int? {
        return config.get(path, ConfigDataType.NUMBER, notFoundError)?.toInt()
    }
}