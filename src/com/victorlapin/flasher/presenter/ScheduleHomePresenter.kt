package com.victorlapin.flasher.presenter

import com.arellomobile.mvp.InjectViewState
import com.victorlapin.flasher.R
import com.victorlapin.flasher.addTo
import com.victorlapin.flasher.manager.SettingsManager
import com.victorlapin.flasher.model.DateBuilder
import com.victorlapin.flasher.model.interactor.AlarmInteractor
import com.victorlapin.flasher.model.interactor.RecoveryScriptInteractor
import com.victorlapin.flasher.model.interactor.ScheduleInteractor
import ru.terrakok.cicerone.Router
import timber.log.Timber

@InjectViewState
class ScheduleHomePresenter constructor(
        router: Router,
        scriptInteractor: RecoveryScriptInteractor,
        settings: SettingsManager,
        interactor: ScheduleInteractor,
        private val mAlarmInteractor: AlarmInteractor
) : BaseHomeFragmentPresenter(router, scriptInteractor, settings, interactor) {
    override val mCurrentFragmentId = R.id.action_schedule

    fun onScheduleEnabledChange(isEnabled: Boolean) {
        mSettings.useSchedule = isEnabled
        if (isEnabled) {
            mAlarmInteractor.setAlarm()
                    .doOnError {
                        Timber.e(it)
                        viewState.showInfoToast(it.localizedMessage)
                    }
                    .subscribe()
                    .addTo(mDisposable)
        } else {
            mAlarmInteractor.cancelAlarm()
                    .doOnError {
                        Timber.e(it)
                        viewState.showInfoToast(it.localizedMessage)
                    }
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
        updateWork()
    }

    fun selectInterval() = viewState.showSelectIntervalDialog(mSettings.scheduleInterval)

    fun onIntervalSelected(interval: Int) {
        mSettings.scheduleInterval = interval
        updateWork()
    }

    fun updateNextRun() {
        val dateBuilder = DateBuilder(mSettings.scheduleTime)
        dateBuilder.interval = mSettings.scheduleInterval
        viewState.showNextRun(dateBuilder.hasNextAlarm(), dateBuilder.nextAlarmTime)
    }

    private fun updateWork() {
        if (mSettings.useSchedule) {
            mAlarmInteractor.setAlarm()
                    .doOnError {
                        Timber.e(it)
                        viewState.showInfoToast(it.localizedMessage)
                    }
                    .subscribe()
                    .addTo(mDisposable)
        }
    }

    fun onOnlyChargingChanged(isChecked: Boolean) {
        mSettings.scheduleOnlyCharging = isChecked
        updateWork()
    }
}