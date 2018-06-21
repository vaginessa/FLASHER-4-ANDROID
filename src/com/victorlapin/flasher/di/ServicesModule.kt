package com.victorlapin.flasher.di

import com.victorlapin.flasher.Screens
import com.victorlapin.flasher.presenter.ScriptTileServicePresenter
import org.koin.dsl.module.applicationContext

val scriptTileServiceModule = applicationContext {
    context(Screens.SERVICE_SCRIPT) {
        factory { ScriptTileServicePresenter(get(), get()) }
    }
}