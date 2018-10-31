package com.victorlapin.flasher.di

import com.victorlapin.flasher.Const
import com.victorlapin.flasher.presenter.*
import org.koin.dsl.module.module

val activitiesModule = module {
    scope(Const.ACTIVITY_MAIN) { MainActivityPresenter(get(), get()) }

    scope(Const.FRAGMENT_HOME) { DefaultHomePresenter(get(), get(), get(), get()) }

    scope(Const.FRAGMENT_SCHEDULE) { ScheduleHomePresenter(get(), get(), get(), get(), get()) }

    scope(Const.FRAGMENT_ABOUT) { AboutFragmentPresenter(get(), get()) }

    scope(Const.ACTIVITY_REBOOT_DIALOG) { RebootDialogActivityPresenter(get(), get()) }
}