package com.victorlapin.flasher.di

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import com.google.gson.GsonBuilder
import com.victorlapin.flasher.model.database.AppDatabase
import com.victorlapin.flasher.model.database.dao.ChainDao
import com.victorlapin.flasher.model.database.dao.CommandDao
import com.victorlapin.flasher.model.database.entity.Chain
import com.victorlapin.flasher.model.database.entity.Command
import com.victorlapin.flasher.model.interactor.*
import com.victorlapin.flasher.model.repository.*
import com.victorlapin.flasher.model.serialization.AnnotationExclusionStrategy
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.koin.dsl.module.module

val modelModule = module {
    single {
        Room.databaseBuilder(get(), AppDatabase::class.java, "flasher.db")
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        Single.create<Any> { emitter ->
                            val chains = ArrayList<Chain>()
                            chains.add(Chain(id = Chain.DEFAULT_ID, name = Chain.DEFAULT))
                            chains.add(Chain(id = Chain.SCHEDULE_ID, name = Chain.SCHEDULE))
                            get<ChainDao>().insert(chains)

                            val data = ArrayList<Command>()
                            data.add(Command(type = Command.TYPE_BACKUP, arg1 = "Boot, Cache, System, Data"))
                            data.add(Command(type = Command.TYPE_WIPE, arg1 = "Cache, Dalvik-cache, System"))
                            data.add(Command(type = Command.TYPE_FLASH_FILE))
                            get<CommandDao>().insert(data)
                            emitter.onSuccess(Any())
                        }
                                .subscribeOn(Schedulers.io())
                                .observeOn(Schedulers.io())
                                .subscribe()
                    }
                })
                .addMigrations(AppDatabase.MIGRATION_1_2)
                .build()
    }
    single { get<AppDatabase>().getCommandDao() }
    single { get<AppDatabase>().getChainDao() }
    single {
        GsonBuilder()
                .serializeNulls()
                .setPrettyPrinting()
                .setExclusionStrategies(AnnotationExclusionStrategy())
                .create()
    }

    factory { CommandsRepository(get(), get()) }
    factory { CommandsInteractor(get()) }
    factory { ScheduleInteractor(get()) }
    factory { AboutRepository(get(), get()) }
    factory { AboutInteractor(get()) }
    factory { RecoveryScriptRepository(get(), get(), get()) }
    factory { RecoveryScriptInteractor(get()) }
    factory { AlarmRepository(get(), get()) }
    factory { AlarmInteractor(get()) }
    factory { BackupsRepository(get()) }
}