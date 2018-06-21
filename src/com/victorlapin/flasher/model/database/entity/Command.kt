package com.victorlapin.flasher.model.database.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.victorlapin.flasher.model.serialization.Exclude

@Entity(tableName = "commands",
        foreignKeys = [(ForeignKey(entity = Chain::class,
                parentColumns = ["id"], childColumns = ["chain_id"],
                onDelete = ForeignKey.NO_ACTION))])

data class Command(
        @PrimaryKey(autoGenerate = true)
        @Exclude
        val id: Long? = null,
        @ColumnInfo(name = "type")
        @SerializedName("type")
        var type: Int,
        @ColumnInfo(name = "arg1")
        @SerializedName("arg1")
        var arg1: String? = null,
        @ColumnInfo(name = "arg2")
        @SerializedName("arg2")
        var arg2: String? = null,
        @ColumnInfo(name = "chain_id")
        @Exclude
        var chainId: Long = Chain.DEFAULT_ID
) {
    companion object {
        const val TYPE_WIPE = 0
        const val TYPE_BACKUP = 1
        const val TYPE_FLASH_FILE = 2
        const val TYPE_FLASH_MASK = 3
    }
}