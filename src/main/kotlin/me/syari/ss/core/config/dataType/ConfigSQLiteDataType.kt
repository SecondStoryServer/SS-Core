package me.syari.ss.core.config.dataType

import me.syari.ss.core.config.CustomConfig
import me.syari.ss.core.sql.SQLite

object ConfigSQLiteDataType : ConfigDataType<SQLite> {
    override val typeName = "SQLite(Section)"

    override fun get(config: CustomConfig, path: String, notFoundError: Boolean): SQLite? {
        return SQLite.create(
            config.plugin.dataFolder,
            config.get("$path.name", ConfigDataType.STRING)
        )
    }
}