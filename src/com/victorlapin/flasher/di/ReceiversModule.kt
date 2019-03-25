package com.victorlapin.flasher.di

import com.victorlapin.flasher.presenter.BootReceiverPresenter
import com.victorlapin.flasher.ui.receivers.BootReceiver
import org.koin.core.qualifier.named
import org.koin.dsl.module

val receiversModule = module {
    scope(named<BootReceiver>()) {
        scoped { BootReceiverPresenter(get(), get(), get()) }
    }
}