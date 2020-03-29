package me.syari.ss.core.config.dataType

import me.syari.ss.core.config.CustomConfig

interface ConfigDataType<T> {
    val typeName: String

    fun get(config: CustomConfig, path: String, notFoundError: Boolean): T?

    fun get(config: CustomConfig, path: String, notFoundError: Boolean, default: T): T {
        return get(config, path, notFoundError) ?: default
    }
}