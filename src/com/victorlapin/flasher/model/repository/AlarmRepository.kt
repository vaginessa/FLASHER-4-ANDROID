package com.victorlapin.flasher.model.repository

import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import com.victorlapin.flasher.manager.SettingsManager
import com.victorlapin.flasher.model.DateBuilder
import com.victorlapin.flasher.work.ScheduleWorker
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class AlarmRepository(
        private val mSettings: SettingsManager
) {
    fun setAlarm(dateBuilder: DateBuilder): Completable =
            Completable.create { emitter ->
                if (dateBuilder.hasNextAlarm()) {
                    val time = dateBuilder.nextAlarmTime
                    Timber.i("Adding schedule worker")
                    Timber.i("Next run: ${SimpleDateFormat.getDateTimeInstance(DateFormat.MEDIUM,
                            DateFormat.SHORT).format(Date(time))}")
                    WorkManager.getInstance()
                            ?.beginUniqueWork(ScheduleWorker.JOB_TAG,
                                    ExistingWorkPolicy.REPLACE,
                                    ScheduleWorker.buildRequest(dateBuilder.nextAlarmTime, mSettings))
                            ?.enqueue()
                }
                emitter.onComplete()
            }
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())

    fun cancelAlarm(): Completable = Completable.create { emitter ->
        Timber.i("Schedule worker canceled")
        WorkManager.getInstance()?.cancelUniqueWork(ScheduleWorker.JOB_TAG)
        emitter.onComplete()
    }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
}