package com.victorlapin.flasher.di

import com.victorlapin.flasher.Screens
import com.victorlapin.flasher.presenter.ScriptTileServicePresenter
import org.koin.dsl.module.module

val servicesModule = module {
    module(Screens.SERVICE_SCRIPT) {
        factory { ScriptTileServicePresenter(get(), get()) }
    }
}