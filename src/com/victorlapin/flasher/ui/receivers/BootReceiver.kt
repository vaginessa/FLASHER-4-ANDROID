package com.victorlapin.flasher.ui.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.victorlapin.flasher.presenter.BootReceiverPresenter
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class BootReceiver : BroadcastReceiver(), KoinComponent {
    private val mPresenter by inject<BootReceiverPresenter>()

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            mPresenter.showNotification()
        }
    }
}