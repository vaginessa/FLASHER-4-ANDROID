package com.victorlapin.flasher.model.repository

import android.app.AlarmManager
import com.victorlapin.flasher.manager.ServicesManager
import com.victorlapin.flasher.manager.SettingsManager
import com.victorlapin.flasher.model.DateBuilder
import io.reactivex.Single
import timber.log.Timber
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class AlarmRepository(
        private val mSettings: SettingsManager,
        private val mServices: ServicesManager
) {
    fun setAlarm(): Single<Any> = Single.create { emitter ->
        val dateBuilder = DateBuilder(mSettings.scheduleTime)
        dateBuilder.interval = mSettings.scheduleInterval

        if (dateBuilder.hasNextAlarm()) {
            val time = dateBuilder.nextAlarmTime
            Timber.tag("Alarm")
                    .i("Next run: ${SimpleDateFormat.getDateTimeInstance(DateFormat.MEDIUM,
                            DateFormat.SHORT).format(Date(time))}")
            if (dateBuilder.interval > 0) {
                mServices.alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, time,
                        TimeUnit.DAYS.toMillis(dateBuilder.interval.toLong()), mServices.alarmIntent)
            } else {
                mServices.alarmManager.setWindow(AlarmManager.RTC_WAKEUP, time,
                        TimeUnit.HOURS.toMillis(1), mServices.alarmIntent)
            }
            mServices.enableBootReceiver()
        }
        emitter.onSuccess(Any())
    }

    fun cancelAlarm(): Single<Any> = Single.create { emitter ->
        Timber.tag("Alarm").i("Canceled")
        mServices.alarmManager.cancel(mServices.alarmIntent)
        mServices.disableBootReceiver()
        emitter.onSuccess(Any())
    }
}