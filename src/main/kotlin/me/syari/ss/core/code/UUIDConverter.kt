package me.syari.ss.core.code

import java.util.*

object UUIDConverter {
    fun String.toUUID() = UUID.fromString(this)

    fun String.toUUIDOrNull() = getUUIDOrNull(this)

    fun getUUIDOrNull(uuid: String): UUID? {
        return try {
            UUID.fromString(uuid)
        } catch (ex: IllegalArgumentException) {
            null
        }
    }
}