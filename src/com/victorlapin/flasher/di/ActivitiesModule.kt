package com.victorlapin.flasher.di

import com.victorlapin.flasher.Screens
import com.victorlapin.flasher.presenter.AboutActivityPresenter
import com.victorlapin.flasher.presenter.AboutFragmentPresenter
import com.victorlapin.flasher.presenter.HomeFragmentPresenter
import com.victorlapin.flasher.presenter.MainActivityPresenter
import com.victorlapin.flasher.ui.adapters.AboutAdapter
import com.victorlapin.flasher.ui.adapters.HomeAdapter
import org.koin.dsl.module.applicationContext

val mainActivityModule = applicationContext {
    context(Screens.ACTIVITY_MAIN) {
        factory { MainActivityPresenter(get(), get()) }

        context(Screens.FRAGMENT_HOME) {
            factory { HomeFragmentPresenter(get(), get()) }
            factory { HomeAdapter(get()) }
        }
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