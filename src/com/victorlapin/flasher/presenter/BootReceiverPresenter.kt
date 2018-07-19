package com.victorlapin.flasher.presenter

import com.victorlapin.flasher.manager.ServicesManager
import com.victorlapin.flasher.manager.SettingsManager
import com.victorlapin.flasher.model.DateBuilder
import com.victorlapin.flasher.model.interactor.AlarmInteractor
import io.reactivex.disposables.Disposable
import timber.log.Timber

class BootReceiverPresenter(
        private val mSettings: SettingsManager,
        private val mServices: ServicesManager,
        private val mAlarmInteractor: AlarmInteractor
) {
    private var mDisposable: Disposable? = null

    fun resetAlarm() {
        val dateBuilder = DateBuilder(mSettings.scheduleTime)
        dateBuilder.interval = mSettings.scheduleInterval
        mDisposable = if (dateBuilder.hasNextAlarm()) {
            mAlarmInteractor.setAlarm()
                    .doOnError { Timber.e(it) }
                    .subscribe()
        } else {
            Timber.i("Nothing to schedule")
            mAlarmInteractor.cancelAlarm()
                    .doOnError { Timber.e(it) }
                    .subscribe()
        }
    }

    fun showNotification() {
        if (mSettings.showNotificationOnBoot && mSettings.bootNotificationFlag) {
            mServices.showBootNotification(mSettings.scheduleLastRun)
        }
        mSettings.bootNotificationFlag = false
    }
}