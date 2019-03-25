package com.victorlapin.flasher.di

import com.victorlapin.flasher.presenter.ScriptTileServicePresenter
import com.victorlapin.flasher.ui.services.ScriptTileService
import org.koin.core.qualifier.named
import org.koin.dsl.module

val servicesModule = module {
    scope(named<ScriptTileService>()) {
        scoped { ScriptTileServicePresenter(get(), get()) }
    }
}