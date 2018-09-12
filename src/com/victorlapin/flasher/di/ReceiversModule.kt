package com.victorlapin.flasher.di

import com.victorlapin.flasher.Screens
import com.victorlapin.flasher.presenter.BootReceiverPresenter
import org.koin.dsl.module.module

val receiversModule = module {
    scope(Screens.RECEIVER_BOOT) { BootReceiverPresenter(get(), get(), get()) }
}