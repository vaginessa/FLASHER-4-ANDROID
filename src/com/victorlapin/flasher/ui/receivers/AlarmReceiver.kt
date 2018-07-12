package com.victorlapin.flasher.ui.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.victorlapin.flasher.presenter.AlarmReceiverPresenter
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import timber.log.Timber

class AlarmReceiver : BroadcastReceiver(), KoinComponent {
    private val mPresenter by inject<AlarmReceiverPresenter>()

    override fun onReceive(context: Context, intent: Intent) {
        Timber.i("Alarm triggered")
        mPresenter.buildAndDeploy()
    }
}