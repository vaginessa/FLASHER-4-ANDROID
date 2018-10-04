package com.victorlapin.flasher.di

import com.victorlapin.flasher.Const
import com.victorlapin.flasher.presenter.BootReceiverPresenter
import org.koin.dsl.module.module

val receiversModule = module {
    scope(Const.RECEIVER_BOOT) { BootReceiverPresenter(get(), get(), get()) }
}