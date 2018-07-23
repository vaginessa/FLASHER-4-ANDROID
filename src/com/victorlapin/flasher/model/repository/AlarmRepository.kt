package com.victorlapin.flasher.model.repository

import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import com.victorlapin.flasher.manager.SettingsManager
import com.victorlapin.flasher.model.DateBuilder
import com.victorlapin.flasher.work.ScheduleStartWorker
import com.victorlapin.flasher.work.ScheduleWorker
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class AlarmRepository {
    fun setAlarm(dateBuilder: DateBuilder): Completable =
            Completable.create { emitter ->
                if (dateBuilder.hasNextAlarm()) {
                    val time = dateBuilder.nextAlarmTime
                    Timber.i("Adding one time worker")
                    Timber.i("Next run: ${SimpleDateFormat.getDateTimeInstance(DateFormat.MEDIUM,
                            DateFormat.SHORT).format(Date(time))}")
                    WorkManager.getInstance()
                            ?.beginUniqueWork(ScheduleStartWorker.JOB_TAG,
                                    ExistingWorkPolicy.REPLACE,
                                    ScheduleStartWorker.buildRequest(dateBuilder.nextAlarmTime))
                            ?.enqueue()
                }
                emitter.onComplete()
            }
                    .mergeWith(cancelPeriodic())
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())

    fun cancelAlarm(): Completable = Completable.create { emitter ->
        Timber.i("One time worker canceled")
        WorkManager.getInstance()?.cancelUniqueWork(ScheduleStartWorker.JOB_TAG)
        emitter.onComplete()
    }
            .mergeWith(cancelPeriodic())
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())

    fun setPeriodic(settings: SettingsManager): Completable = Completable.create { emitter ->
        Timber.i("Adding periodic worker")
        WorkManager.getInstance()
                ?.enqueueUniquePeriodicWork(ScheduleWorker.JOB_TAG,
                        ExistingPeriodicWorkPolicy.REPLACE,
                        ScheduleWorker.buildRequest(settings))
        emitter.onComplete()
    }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())

    private fun cancelPeriodic(): Completable = Completable.create { emitter ->
        Timber.i("Periodic worker canceled")
        WorkManager.getInstance()?.cancelUniqueWork(ScheduleWorker.JOB_TAG)
        emitter.onComplete()
    }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
}