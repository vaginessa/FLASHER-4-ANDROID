package com.victorlapin.flasher.model.repository

import android.app.AlarmManager
import android.util.Log
import com.victorlapin.flasher.manager.ServicesManager
import com.victorlapin.flasher.manager.SettingsManager
import io.reactivex.Single
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class AlarmRepository(
        private val mSettings: SettingsManager,
        private val mServices: ServicesManager
) {
    fun setAlarm(): Single<Any> = Single.create { emitter ->
        val time = mSettings.scheduleTime
        val intervalDays = mSettings.scheduleInterval

        if (time > 0) {
            Log.i("Alarm",
                    "Next run: ${SimpleDateFormat.getDateTimeInstance(DateFormat.MEDIUM,
                            DateFormat.SHORT).format(Date(time))}")
            if (intervalDays > 0) {
                mServices.alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, time,
                        TimeUnit.DAYS.toMillis(intervalDays.toLong()), mServices.alarmIntent)
            } else {
                mServices.alarmManager.setWindow(AlarmManager.RTC_WAKEUP, time,
                        TimeUnit.HOURS.toMillis(1), mServices.alarmIntent)
            }
            mServices.enableBootReceiver()
        }
        emitter.onSuccess(Any())
    }

    fun cancelAlarm(): Single<Any> = Single.create { emitter ->
        Log.i("Alarm", "Canceled")
        mServices.alarmManager.cancel(mServices.alarmIntent)
        mServices.disableBootReceiver()
        emitter.onSuccess(Any())
    }
}