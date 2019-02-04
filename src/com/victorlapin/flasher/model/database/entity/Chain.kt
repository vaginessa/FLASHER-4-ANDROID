package com.victorlapin.flasher.model.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "chains")
data class Chain(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    val id: Long? = null,
    @ColumnInfo(name = "name")
    @SerializedName("name")
    var name: String
) {
    companion object {
        const val DEFAULT = "default"
        const val SCHEDULE = "schedule"
        const val DEFAULT_ID = 1L
        const val SCHEDULE_ID = 2L
    }
}