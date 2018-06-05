package com.victorlapin.flasher.di

import com.victorlapin.flasher.manager.SettingsManager
import org.koin.dsl.module.applicationContext
import ru.terrakok.cicerone.Cicerone
import ru.terrakok.cicerone.Router

val appModule = applicationContext {
    bean { Cicerone.create() as Cicerone<Router> }
    bean { get<Cicerone<Router>>().router }
    bean { get<Cicerone<Router>>().navigatorHolder }

    bean { SettingsManager(get()) }
}