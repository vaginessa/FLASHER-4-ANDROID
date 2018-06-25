package com.victorlapin.flasher.presenter

import com.victorlapin.flasher.manager.SettingsManager
import com.victorlapin.flasher.model.DateBuilder
import com.victorlapin.flasher.model.interactor.AlarmInteractor

class AlarmBootReceiverPresenter(
        private val mSettings: SettingsManager,
        private val mAlarmInteractor: AlarmInteractor
) {
    fun resetAlarm() {
        val time = mSettings.scheduleTime
        if (time > 0) {
            val dateBuilder = DateBuilder(time)
            mSettings.scheduleTime = dateBuilder.nextAlarmTime
            mAlarmInteractor.setAlarm()
                    .doOnError { it.printStackTrace() }
                    .subscribe()
        }
    }
}