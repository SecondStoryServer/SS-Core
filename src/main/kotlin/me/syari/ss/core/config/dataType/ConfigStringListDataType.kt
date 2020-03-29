package me.syari.ss.core.config.dataType

import me.syari.ss.core.config.CustomConfig

object ConfigStringListDataType : ConfigDataType<List<String>> {
    override val typeName = "List<String>"

    override fun get(config: CustomConfig, path: String, notFoundError: Boolean): List<String>? {
        return config.getListUnsafe(path, typeName, notFoundError)
    }
}