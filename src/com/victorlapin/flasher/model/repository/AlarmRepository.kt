package com.victorlapin.flasher.model.repository

import android.app.AlarmManager
import com.victorlapin.flasher.manager.ServicesManager
import com.victorlapin.flasher.manager.SettingsManager
import io.reactivex.Single
import java.util.concurrent.TimeUnit

class AlarmRepository(
        private val mSettings: SettingsManager,
        private val mServices: ServicesManager
) {
    fun setAlarm(): Single<Any> = Single.create { emitter ->
        val time = mSettings.scheduleTime
        val periodDays = mSettings.schedulePeriod

        if (time > 0) {
            if (periodDays > 0) {
                mServices.alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, time,
                        TimeUnit.DAYS.toMillis(periodDays.toLong()), mServices.alarmIntent)
            } else {
                mServices.alarmManager.setWindow(AlarmManager.RTC_WAKEUP, time,
                        TimeUnit.HOURS.toMillis(1), mServices.alarmIntent)
            }
        }
        emitter.onSuccess(Any())
    }

    fun cancelAlarm(): Single<Any> = Single.create { emitter ->
        mServices.alarmManager.cancel(mServices.alarmIntent)
        emitter.onSuccess(Any())
    }
}