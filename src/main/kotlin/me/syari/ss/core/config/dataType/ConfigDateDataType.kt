package me.syari.ss.core.config.dataType

import me.syari.ss.core.config.CustomConfig
import java.util.*

object ConfigDateDataType : ConfigDataType<Date> {
    override val typeName = "Date"

    override fun get(config: CustomConfig, path: String, notFoundError: Boolean): Date? {
        return config.getUnsafe(path, typeName, notFoundError)
    }
}