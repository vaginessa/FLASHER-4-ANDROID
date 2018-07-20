package com.victorlapin.flasher.presenter

import com.victorlapin.flasher.manager.ServicesManager
import com.victorlapin.flasher.manager.SettingsManager
import com.victorlapin.flasher.model.interactor.AlarmInteractor
import timber.log.Timber

class BootReceiverPresenter(
        private val mSettings: SettingsManager,
        private val mServices: ServicesManager,
        private val mAlarmInteractor: AlarmInteractor
) {
    fun resetAlarm() {
        mAlarmInteractor.setAlarm()
                .doOnError { Timber.e(it) }
                .subscribe()
    }

    fun showNotification() {
        if (mSettings.showNotificationOnBoot && mSettings.bootNotificationFlag) {
            mServices.showBootNotification(mSettings.scheduleLastRun)
        }
        mSettings.bootNotificationFlag = false
    }
}