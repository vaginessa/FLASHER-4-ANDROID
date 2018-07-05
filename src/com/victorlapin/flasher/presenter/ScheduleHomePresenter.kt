package com.victorlapin.flasher.presenter

import com.arellomobile.mvp.InjectViewState
import com.victorlapin.flasher.addTo
import com.victorlapin.flasher.manager.SettingsManager
import com.victorlapin.flasher.model.DateBuilder
import com.victorlapin.flasher.model.interactor.AlarmInteractor
import com.victorlapin.flasher.model.interactor.RecoveryScriptInteractor
import com.victorlapin.flasher.model.interactor.ScheduleInteractor

@InjectViewState
class ScheduleHomePresenter constructor(
        mScriptInteractor: RecoveryScriptInteractor,
        mSettings: SettingsManager,
        mInteractor: ScheduleInteractor,
        private val mAlarmInteractor: AlarmInteractor
) : BaseHomeFragmentPresenter(mScriptInteractor, mSettings, mInteractor) {
    fun onScheduleEnabledChange(isEnabled: Boolean) {
        mSettings.useSchedule = isEnabled
        if (isEnabled) {
            mAlarmInteractor.setAlarm()
                    .doOnError { viewState.showInfoToast(it.localizedMessage) }
                    .subscribe()
                    .addTo(mDisposable)
        } else {
            mAlarmInteractor.cancelAlarm()
                    .doOnError { viewState.showInfoToast(it.localizedMessage) }
                    .subscribe()
                    .addTo(mDisposable)
        }
    }

    fun selectTime() {
        val dateBuilder = DateBuilder(mSettings.scheduleTime)
        viewState.showSelectTimeDialog(
                dateBuilder.hourOfDay,
                dateBuilder.minute)
    }

    fun onTimeSelected(hourOfDay: Int, minute: Int) {
        val dateBuilder = DateBuilder(hourOfDay, minute)
        mSettings.scheduleTime = dateBuilder.nextAlarmTime

        if (mSettings.useSchedule) {
            mAlarmInteractor.setAlarm()
                    .doOnError { viewState.showInfoToast(it.localizedMessage) }
                    .subscribe()
                    .addTo(mDisposable)
        }
    }

    fun selectInterval() = viewState.showSelectIntervalDialog(mSettings.scheduleInterval)

    fun onIntervalSelected(interval: Int) {
        mSettings.scheduleInterval = interval
        if (mSettings.useSchedule) {
            mAlarmInteractor.setAlarm()
                    .doOnError { viewState.showInfoToast(it.localizedMessage) }
                    .subscribe()
                    .addTo(mDisposable)
        }
    }

    fun updateNextRun() {
        val dateBuilder = DateBuilder(mSettings.scheduleTime)
        dateBuilder.interval = mSettings.scheduleInterval
        viewState.showNextRun(dateBuilder.hasNextAlarm(), dateBuilder.nextAlarmTime)
    }
}