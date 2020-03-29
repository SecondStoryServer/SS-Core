package me.syari.ss.core.config.dataType

import me.syari.ss.core.config.CustomConfig

object ConfigBooleanDataType : ConfigDataType<Boolean> {
    override val typeName = "Boolean"

    override fun get(config: CustomConfig, path: String, notFoundError: Boolean): Boolean? {
        return config.getUnsafe(path, typeName, notFoundError)
    }
}