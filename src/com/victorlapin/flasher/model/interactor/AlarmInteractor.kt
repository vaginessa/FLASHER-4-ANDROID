package com.victorlapin.flasher.model.interactor

import com.victorlapin.flasher.manager.ServicesManager
import com.victorlapin.flasher.manager.SettingsManager
import com.victorlapin.flasher.model.DateBuilder
import com.victorlapin.flasher.model.repository.AlarmRepository
import io.reactivex.Completable
import timber.log.Timber

class AlarmInteractor(
    private val mRepo: AlarmRepository,
    private val mSettings: SettingsManager,
    private val mServices: ServicesManager
) {
    fun setAlarm(): Completable {
        val dateBuilder = DateBuilder(mSettings.scheduleTime)
        dateBuilder.interval = mSettings.scheduleInterval
        return if (dateBuilder.hasNextAlarm()) {
            mRepo.setAlarm(dateBuilder, mSettings)
                .doOnComplete { mServices.enableBootReceiver() }
        } else {
            Timber.i("Nothing to schedule")
            cancelAlarm()
        }
    }

    fun cancelAlarm(): Completable = mRepo.cancelAlarm()
        .doOnComplete { mServices.disableBootReceiver() }
}