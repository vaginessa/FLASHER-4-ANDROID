package com.victorlapin.flasher.di

import com.victorlapin.flasher.presenter.AboutFragmentPresenter
import com.victorlapin.flasher.presenter.DefaultHomePresenter
import com.victorlapin.flasher.presenter.MainActivityPresenter
import com.victorlapin.flasher.presenter.RebootDialogActivityPresenter
import com.victorlapin.flasher.presenter.ScheduleHomePresenter
import com.victorlapin.flasher.ui.activities.MainActivity
import com.victorlapin.flasher.ui.activities.RebootDialogActivity
import com.victorlapin.flasher.ui.fragments.AboutFragment
import com.victorlapin.flasher.ui.fragments.HomeFragment
import com.victorlapin.flasher.ui.fragments.ScheduleFragment
import org.koin.core.qualifier.named
import org.koin.dsl.module

val activitiesModule = module {
    scope(named<MainActivity>()) {
        scoped { MainActivityPresenter(get(), get(), get()) }
    }

    scope(named<HomeFragment>()) {
        scoped { DefaultHomePresenter(get(), get(), get(), get()) }
    }

    scope(named<ScheduleFragment>()) {
        scoped { ScheduleHomePresenter(get(), get(), get(), get(), get()) }
    }

    scope(named<AboutFragment>()) {
        scoped { AboutFragmentPresenter(get(), get()) }
    }

    scope(named<RebootDialogActivity>()) {
        scoped { RebootDialogActivityPresenter(get(), get()) }
    }
}