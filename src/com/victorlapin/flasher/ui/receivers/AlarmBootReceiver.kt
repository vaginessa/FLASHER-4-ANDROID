package com.victorlapin.flasher.ui.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.victorlapin.flasher.presenter.AlarmBootReceiverPresenter
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class AlarmBootReceiver : BroadcastReceiver(), KoinComponent {
    private val mPresenter by inject<AlarmBootReceiverPresenter>()

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            mPresenter.resetAlarm()
            mPresenter.showNotification()
        }
    }
}