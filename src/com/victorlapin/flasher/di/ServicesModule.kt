package com.victorlapin.flasher.di

import com.victorlapin.flasher.Const
import com.victorlapin.flasher.presenter.ScriptTileServicePresenter
import org.koin.dsl.module.module

val servicesModule = module {
    scope(Const.SERVICE_SCRIPT) { ScriptTileServicePresenter(get(), get()) }
}