package com.victorlapin.flasher.di

import com.victorlapin.flasher.Screens
import com.victorlapin.flasher.presenter.ScriptTileServicePresenter
import org.koin.dsl.module.module

val servicesModule = module {
    scope(Screens.SERVICE_SCRIPT) { ScriptTileServicePresenter(get(), get()) }
}