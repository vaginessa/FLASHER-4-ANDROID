package com.victorlapin.flasher.model.database.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "commands")
data class Command(
        @PrimaryKey(autoGenerate = true)
        val id: Long? = null,
        @ColumnInfo(name = "type")
        var type: Int,
        @ColumnInfo(name = "arg1")
        var arg1: String? = null,
        @ColumnInfo(name = "arg2")
        var arg2: String? = null
) {
    companion object {
        const val TYPE_WIPE = 0
        const val TYPE_BACKUP = 1
        const val TYPE_FLASH_FILE = 2
        const val TYPE_FLASH_MASK = 3
    }
}