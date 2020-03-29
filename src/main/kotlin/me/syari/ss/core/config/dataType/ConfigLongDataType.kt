package me.syari.ss.core.config.dataType

import me.syari.ss.core.config.CustomConfig

object ConfigLongDataType : ConfigDataType<Long> {
    override val typeName = "Long"

    override fun get(config: CustomConfig, path: String, notFoundError: Boolean): Long? {
        return config.get(path, CustomConfig.NUMBER, notFoundError)?.toLong()
    }
}