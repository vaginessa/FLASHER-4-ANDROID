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
import org.koin.dsl.module

val activitiesModule = module {
    scope<MainActivity> {
        scoped { MainActivityPresenter(get(), get(), get()) }
    }

    scope<HomeFragment> {
        scoped { DefaultHomePresenter(get(), get(), get(), get()) }
    }

    scope<ScheduleFragment> {
        scoped { ScheduleHomePresenter(get(), get(), get(), get(), get()) }
    }

    scope<AboutFragment> {
        scoped { AboutFragmentPresenter(get(), get()) }
    }

    scope<RebootDialogActivity> {
        scoped { RebootDialogActivityPresenter(get(), get()) }
    }
}