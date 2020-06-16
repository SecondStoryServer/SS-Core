package me.syari.ss.core.config.dataType

import me.syari.ss.core.config.CustomConfig

object ConfigStringDataType: ConfigDataType<String> {
    override val typeName = "String"

    override fun get(
        config: CustomConfig,
        path: String,
        notFoundError: Boolean
    ): String? {
        return config.getUnsafe<String>(path, typeName, notFoundError)
    }
}