package com.victorlapin.flasher.presenter

import com.victorlapin.flasher.manager.ServicesManager
import com.victorlapin.flasher.manager.SettingsManager

class BootReceiverPresenter(
        private val mSettings: SettingsManager,
        private val mServices: ServicesManager
) {
    fun showNotification() {
        if (mSettings.showNotificationOnBoot && mSettings.bootNotificationFlag) {
            mServices.showBootNotification(mSettings.scheduleLastRun)
        }
        mSettings.bootNotificationFlag = false
    }
}