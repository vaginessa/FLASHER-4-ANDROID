package com.victorlapin.flasher.di

import com.victorlapin.flasher.Screens
import com.victorlapin.flasher.presenter.*
import com.victorlapin.flasher.ui.adapters.AboutAdapter
import com.victorlapin.flasher.ui.adapters.HomeAdapter
import org.koin.dsl.module.module

val activitiesModule = module {
    scope(Screens.ACTIVITY_MAIN) { MainActivityPresenter(get()) }

    scope(Screens.FRAGMENT_HOME) { DefaultHomePresenter(get(), get(), get(), get()) }
    factory { HomeAdapter(get()) }

    scope(Screens.FRAGMENT_SCHEDULE) { ScheduleHomePresenter(get(), get(), get(), get(), get()) }

    scope(Screens.FRAGMENT_ABOUT) { AboutFragmentPresenter(get(), get()) }
    scope(Screens.FRAGMENT_ABOUT) { AboutAdapter(get()) }

    scope(Screens.ACTIVITY_REBOOT_DIALOG) { RebootDialogActivityPresenter(get()) }
}