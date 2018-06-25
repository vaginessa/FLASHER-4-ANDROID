package com.victorlapin.flasher.di

import com.victorlapin.flasher.Screens
import com.victorlapin.flasher.presenter.AlarmBootReceiverPresenter
import com.victorlapin.flasher.presenter.AlarmReceiverPresenter
import org.koin.dsl.module.applicationContext

val alarmReceiverModule = applicationContext {
    context(Screens.RECEIVER_ALARM) {
        factory { AlarmReceiverPresenter(get(), get()) }
    }

    context(Screens.RECEIVER_BOOT_ALARM) {
        factory { AlarmBootReceiverPresenter(get(), get()) }
    }
}