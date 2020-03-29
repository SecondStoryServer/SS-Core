package me.syari.ss.core.config.dataType

import me.syari.ss.core.config.CustomConfig

object ConfigNumberDataType : ConfigDataType<Number> {
    override val typeName = "Number"

    override fun get(config: CustomConfig, path: String, notFoundError: Boolean): Number? {
        return config.getUnsafe(path, typeName, notFoundError)
    }
}