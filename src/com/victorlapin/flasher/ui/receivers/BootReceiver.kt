package com.victorlapin.flasher.ui.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.victorlapin.flasher.presenter.BootReceiverPresenter
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import timber.log.Timber

class BootReceiver : BroadcastReceiver(), KoinComponent {
    private val mPresenter by inject<BootReceiverPresenter>()

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            Timber.i("Recreating scheduled job on boot")
            mPresenter.resetAlarm()
            mPresenter.showNotification()
        }
    }
}