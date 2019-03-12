package com.victorlapin.flasher.di

import com.victorlapin.flasher.presenter.ScriptTileServicePresenter
import com.victorlapin.flasher.ui.services.ScriptTileService
import org.koin.dsl.module

val servicesModule = module {
    scope<ScriptTileService> {
        scoped { ScriptTileServicePresenter(get(), get()) }
    }
}