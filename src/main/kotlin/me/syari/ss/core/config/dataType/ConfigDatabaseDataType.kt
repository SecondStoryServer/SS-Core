package me.syari.ss.core.config.dataType

import me.syari.ss.core.config.CustomConfig
import me.syari.ss.core.sql.Database
import me.syari.ss.core.sql.MySQL
import me.syari.ss.core.sql.SQLite

object ConfigDatabaseDataType : ConfigDataType<Database> {
    override val typeName = "Database(Section)"

    override fun get(config: CustomConfig, path: String, notFoundError: Boolean): Database? {
        if (!config.contains(path)) return null
        return when (config.get("$path.type", ConfigDataType.STRING, "mysql", false)) {
            "mysql" -> {
                MySQL.create(
                    config.get("$path.host", ConfigDataType.STRING),
                    config.get("$path.port", ConfigDataType.INT),
                    config.get("$path.database", ConfigDataType.STRING),
                    config.get("$path.user", ConfigDataType.STRING),
                    config.get("$path.password", ConfigDataType.STRING)
                )
            }
            "sqlite" -> {
                SQLite.create(
                    config.plugin.dataFolder,
                    config.get("$path.name", ConfigDataType.STRING)
                )
            }
            else -> {
                config.nullError("$path.type", "DatabaseType(mysql, sqlite)")
                null
            }
        }
    }
}