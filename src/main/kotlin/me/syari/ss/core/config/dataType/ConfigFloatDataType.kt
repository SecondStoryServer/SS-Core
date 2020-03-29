package me.syari.ss.core.config.dataType

import me.syari.ss.core.config.CustomConfig

object ConfigFloatDataType : ConfigDataType<Float> {
    override val typeName = "Float"

    override fun get(config: CustomConfig, path: String, notFoundError: Boolean): Float? {
        return config.get(path, CustomConfig.NUMBER, notFoundError)?.toFloat()
    }
}