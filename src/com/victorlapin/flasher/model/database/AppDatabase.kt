package com.victorlapin.flasher.model.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.victorlapin.flasher.model.database.dao.CommandDao
import com.victorlapin.flasher.model.database.entity.Command

@Database(entities = [
    Command::class
], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getCommandDao(): CommandDao
}