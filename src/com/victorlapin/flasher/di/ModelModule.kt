package com.victorlapin.flasher.di

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import com.victorlapin.flasher.model.database.AppDatabase
import com.victorlapin.flasher.model.database.dao.CommandDao
import com.victorlapin.flasher.model.database.entity.Command
import com.victorlapin.flasher.model.interactor.CommandsInteractor
import com.victorlapin.flasher.model.repository.CommandsRepository
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.koin.dsl.module.applicationContext

val modelModule = applicationContext {
    bean {
        Room.databaseBuilder(get(), AppDatabase::class.java, "flasher.db")
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        Single.create<Any> { emitter ->
                            val data = ArrayList<Command>()
                            data.add(Command(type = Command.TYPE_BACKUP, arg1 = "Boot, Cache, System, Data"))
                            data.add(Command(type = Command.TYPE_WIPE, arg1 = "Cache, Dalvik-cache, System"))
                            get<CommandDao>().insert(data)
                            emitter.onSuccess(Any())
                        }
                                .subscribeOn(Schedulers.io())
                                .observeOn(Schedulers.io())
                                .subscribe()
                    }
                })
                .build()
    }
    bean { get<AppDatabase>().getCommandDao() }

    factory { CommandsRepository(get()) }
    factory { CommandsInteractor(get()) }
}