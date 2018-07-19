package com.victorlapin.flasher.di

import com.victorlapin.flasher.Screens
import com.victorlapin.flasher.presenter.BootReceiverPresenter
import org.koin.dsl.module.module

val receiversModule = module {
    module(Screens.RECEIVER_BOOT) {
        factory { BootReceiverPresenter(get(), get(), get()) }
    }
}