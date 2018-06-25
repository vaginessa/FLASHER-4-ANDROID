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
        val period = mSettings.schedulePeriod
        if (time > 0) {
            val dateBuilder = DateBuilder(time)
            when {
                (time > System.currentTimeMillis()) -> {
                    mAlarmInteractor.setAlarm()
                            .doOnError { it.printStackTrace() }
                            .subscribe()
                }
                (period > 0) -> {
                    dateBuilder.period = period
                    mSettings.scheduleTime = dateBuilder.nextAlarmTime
                    mAlarmInteractor.setAlarm()
                            .doOnError { it.printStackTrace() }
                            .subscribe()
                }
                else -> {
                    mSettings.useSchedule = false
                    mAlarmInteractor.cancelAlarm()
                            .doOnError { it.printStackTrace() }
                            .subscribe()
                }
            }
        }
    }
}