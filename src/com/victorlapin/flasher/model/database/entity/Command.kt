package com.victorlapin.flasher.model.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.victorlapin.flasher.model.serialization.Exclude

@Entity(tableName = "commands",
        foreignKeys = [(ForeignKey(entity = Chain::class,
                parentColumns = ["id"], childColumns = ["chain_id"],
                onDelete = ForeignKey.NO_ACTION))])

data class Command(
        @PrimaryKey(autoGenerate = true)
        @Exclude
        var id: Long? = null,
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
        var chainId: Long = Chain.DEFAULT_ID,
        @ColumnInfo(name = "order_number")
        @Exclude
        var orderNumber: Int = 0
) {
    fun clone() = Command(
            id = this.id,
            type = this.type,
            arg1 = this.arg1,
            arg2 = this.arg2,
            chainId = this.chainId,
            orderNumber = this.orderNumber)

    companion object {
        const val TYPE_WIPE = 0
        const val TYPE_BACKUP = 1
        const val TYPE_FLASH_FILE = 2
        const val TYPE_FLASH_MASK = 3
    }
}