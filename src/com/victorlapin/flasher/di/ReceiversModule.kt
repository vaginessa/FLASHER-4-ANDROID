package com.victorlapin.flasher.di

import com.victorlapin.flasher.Screens
import com.victorlapin.flasher.presenter.AlarmBootReceiverPresenter
import com.victorlapin.flasher.presenter.AlarmReceiverPresenter
import org.koin.dsl.module.module

val receiversModule = module {
    module(Screens.RECEIVER_ALARM) {
        factory { AlarmReceiverPresenter(get(), get()) }
    }

    module(Screens.RECEIVER_BOOT_ALARM) {
        factory { AlarmBootReceiverPresenter(get(), get(), get()) }
    }
}