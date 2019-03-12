package com.victorlapin.flasher.di

import com.victorlapin.flasher.manager.LogManager
import com.victorlapin.flasher.manager.ResourcesManager
import com.victorlapin.flasher.manager.ServicesManager
import com.victorlapin.flasher.manager.SettingsManager
import org.koin.dsl.module
import ru.terrakok.cicerone.Cicerone
import ru.terrakok.cicerone.Router

val appModule = module {
    single<Cicerone<Router>> { Cicerone.create() }
    single { get<Cicerone<Router>>().router }
    single { get<Cicerone<Router>>().navigatorHolder }

    single { SettingsManager(get()) }
    single { ResourcesManager(get()) }
    single { ServicesManager(get()) }
    single { LogManager(get()) }
}