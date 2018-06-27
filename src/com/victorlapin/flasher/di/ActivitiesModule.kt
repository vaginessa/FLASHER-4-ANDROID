package com.victorlapin.flasher.di

import com.victorlapin.flasher.Screens
import com.victorlapin.flasher.presenter.*
import com.victorlapin.flasher.ui.adapters.AboutAdapter
import com.victorlapin.flasher.ui.adapters.HomeAdapter
import org.koin.dsl.module.module

val activitiesModule = module {
    module(Screens.ACTIVITY_MAIN) {
        factory { MainActivityPresenter(get(), get(), get()) }
    }

    module(Screens.FRAGMENT_HOME) {
        factory { DefaultHomePresenter(get(), get(), get(), get()) }
        factory { HomeAdapter(get()) }
    }

    module(Screens.FRAGMENT_SCHEDULE) {
        factory { ScheduleHomePresenter(get(), get(), get(), get(), get()) }
    }

    module(Screens.ACTIVITY_SETTINGS) {
        factory { SettingsActivityPresenter(get()) }
    }

    module(Screens.ACTIVITY_ABOUT) {
        factory { AboutActivityPresenter(get()) }
    }

    module(Screens.FRAGMENT_ABOUT) {
        factory { AboutFragmentPresenter(get(), get()) }
        factory { AboutAdapter(get()) }
    }

    module(Screens.ACTIVITY_REBOOT_DIALOG) {
        factory { RebootDialogActivityPresenter(get()) }
    }
}