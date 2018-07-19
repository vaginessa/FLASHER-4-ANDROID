package com.victorlapin.flasher.model.repository

import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import com.victorlapin.flasher.manager.ServicesManager
import com.victorlapin.flasher.manager.SettingsManager
import com.victorlapin.flasher.model.DateBuilder
import com.victorlapin.flasher.work.ScheduleWorker
import io.reactivex.Single
import timber.log.Timber
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class AlarmRepository(
        private val mSettings: SettingsManager,
        private val mServices: ServicesManager
) {
    fun setAlarm(): Single<Any> = Single.create { emitter ->
        val dateBuilder = DateBuilder(mSettings.scheduleTime)
        dateBuilder.interval = mSettings.scheduleInterval

        if (dateBuilder.hasNextAlarm()) {
            val time = dateBuilder.nextAlarmTime
            Timber.i("Next run: ${SimpleDateFormat.getDateTimeInstance(DateFormat.MEDIUM,
                            DateFormat.SHORT).format(Date(time))}")
            WorkManager.getInstance()
                    ?.beginUniqueWork(ScheduleWorker.JOB_TAG,
                            ExistingWorkPolicy.REPLACE,
                            ScheduleWorker.buildRequest(time, mSettings))
                    ?.enqueue()
            mServices.enableBootReceiver()
        }
        emitter.onSuccess(Any())
    }

    fun cancelAlarm(): Single<Any> = Single.create { emitter ->
        Timber.i("Canceled")
        WorkManager.getInstance()?.cancelUniqueWork(ScheduleWorker.JOB_TAG)
        mServices.disableBootReceiver()
        emitter.onSuccess(Any())
    }
}