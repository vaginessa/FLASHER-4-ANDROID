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
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import org.koin.dsl.module.module

val modelModule = module {
    single {
        Room.databaseBuilder(get(), AppDatabase::class.java, "flasher.db")
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        Completable.create { emitter ->
                            val chains = ArrayList<Chain>()
                            chains.add(Chain(id = Chain.DEFAULT_ID, name = Chain.DEFAULT))
                            chains.add(Chain(id = Chain.SCHEDULE_ID, name = Chain.SCHEDULE))
                            get<ChainDao>().insert(chains)

                            val data = ArrayList<Command>()
                            data.add(Command(type = Command.TYPE_BACKUP, arg1 = "Boot, Cache, System, Data", orderNumber = 1))
                            data.add(Command(type = Command.TYPE_WIPE, arg1 = "Cache, Dalvik-cache, System", orderNumber = 2))
                            data.add(Command(type = Command.TYPE_FLASH_FILE, orderNumber = 3))
                            data.add(Command(type = Command.TYPE_BACKUP, arg1 = "Boot, Cache, System, Data",
                                    chainId = Chain.SCHEDULE_ID, orderNumber = 0))
                            get<CommandDao>().insert(data)
                            emitter.onComplete()
                        }
                                .subscribeOn(Schedulers.io())
                                .observeOn(Schedulers.io())
                                .subscribe()
                    }
                })
                .addMigrations(AppDatabase.MIGRATION_1_2, AppDatabase.MIGRATION_2_3)
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

    factory { CommandsRepository(get()) }
    factory { HomeInteractor(get(), get()) }
    factory { ScheduleInteractor(get(), get()) }
    factory { AboutRepository(get(), get()) }
    factory { AboutInteractor(get()) }
    factory { RecoveryScriptRepository() }
    factory { RecoveryScriptInteractor(get(), get(), get(), get()) }
    factory { AlarmRepository() }
    factory { AlarmInteractor(get(), get(), get()) }
    factory { BackupsRepository() }
}