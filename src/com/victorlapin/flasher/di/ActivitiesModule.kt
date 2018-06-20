package com.victorlapin.flasher.di

import com.victorlapin.flasher.Screens
import com.victorlapin.flasher.presenter.*
import com.victorlapin.flasher.ui.adapters.AboutAdapter
import com.victorlapin.flasher.ui.adapters.HomeAdapter
import org.koin.dsl.module.applicationContext

val mainActivityModule = applicationContext {
    context(Screens.ACTIVITY_MAIN) {
        factory { MainActivityPresenter(get(), get(), get()) }

        context(Screens.FRAGMENT_HOME) {
            factory { DefaultHomePresenter(get(), get(), get(), get()) }
            factory { ScheduleHomePresenter(get(), get(), get(), get()) }
            factory { HomeAdapter(get()) }
        }
    }
}

val settingsActivityModule = applicationContext {
    context(Screens.ACTIVITY_SETTINGS) {
        factory { SettingsActivityPresenter(get()) }
    }
}

val aboutActivityModule = applicationContext {
    context(Screens.ACTIVITY_ABOUT) {
        factory { AboutActivityPresenter(get()) }

        context(Screens.FRAGMENT_ABOUT) {
            factory { AboutFragmentPresenter(get(), get()) }
            factory { AboutAdapter(get()) }
        }
    }
}

val rebootDialogActivityModule = applicationContext {
    context(Screens.ACTIVITY_REBOOT_DIALOG) {
        factory { RebootDialogActivityPresenter(get()) }
    }
}