package com.victorlapin.flasher.di

import android.arch.persistence.room.Room
import com.victorlapin.flasher.model.database.AppDatabase
import org.koin.dsl.module.applicationContext

val modelModule = applicationContext {
    bean {
        Room.databaseBuilder(get(), AppDatabase::class.java, "flasher.db")
                .build()
    }
    bean { get<AppDatabase>().getCommandDao() }
}