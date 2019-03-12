package com.victorlapin.flasher.ui.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.victorlapin.flasher.presenter.BootReceiverPresenter
import org.koin.core.KoinComponent
import timber.log.Timber

class BootReceiver : BroadcastReceiver(), KoinComponent {
    private var mScope = getKoin().createScopeWithType<BootReceiver>("")
    private val mPresenter by mScope.inject<BootReceiverPresenter>()

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            Timber.i("Recreating schedule worker on boot")
            mPresenter.resetAlarm()
            mPresenter.showNotification()
        }
        mScope.close()
    }
}