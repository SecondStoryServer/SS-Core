package me.syari.ss.core.persistentData.customType

import org.bukkit.persistence.PersistentDataType
import java.util.*

object CustomPersistentDataType {
    val UUID: PersistentDataType<ByteArray, UUID> =
        PersistentDataTypeUUID()
}