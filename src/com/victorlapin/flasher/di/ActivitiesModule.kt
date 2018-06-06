package com.victorlapin.flasher.di

import com.victorlapin.flasher.Screens
import com.victorlapin.flasher.presenter.HomeFragmentPresenter
import com.victorlapin.flasher.presenter.MainActivityPresenter
import com.victorlapin.flasher.ui.adapters.HomeAdapter
import org.koin.dsl.module.applicationContext

val mainActivityModule = applicationContext {
    context(Screens.ACTIVITY_MAIN) {
        factory { MainActivityPresenter(get(), get()) }

        context(Screens.FRAGMENT_HOME) {
            factory { HomeFragmentPresenter(get()) }
            factory { HomeAdapter(get()) }
        }
    }
}