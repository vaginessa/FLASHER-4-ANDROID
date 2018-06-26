package com.victorlapin.flasher.presenter

import com.arellomobile.mvp.InjectViewState
import com.victorlapin.flasher.addTo
import com.victorlapin.flasher.manager.SettingsManager
import com.victorlapin.flasher.model.DateBuilder
import com.victorlapin.flasher.model.database.entity.Command
import com.victorlapin.flasher.model.interactor.AlarmInteractor
import com.victorlapin.flasher.model.interactor.RecoveryScriptInteractor
import com.victorlapin.flasher.model.interactor.ScheduleInteractor
import com.victorlapin.flasher.view.HomeFragmentView
import ru.terrakok.cicerone.Router

@InjectViewState
class ScheduleHomePresenter constructor(
        mScriptInteractor: RecoveryScriptInteractor,
        mSettings: SettingsManager,
        mRouter: Router,
        private val mInteractor: ScheduleInteractor,
        private val mAlarmInteractor: AlarmInteractor
) : HomeFragmentPresenter(mRouter, mScriptInteractor, mSettings) {
    override fun attachView(view: HomeFragmentView?) {
        super.attachView(view)
        mInteractor.getSchedule()
                .subscribe {
                    viewState.setData(it, mFirstRun)
                    mFirstRun = false
                }
                .addTo(mDisposable)
    }

    override fun onCommandUpdated(command: Command) =
            mInteractor.updateCommand(command)

    override fun onCommandSwiped(id: Long) {
        mInteractor.getCommand(id)
                .subscribe {
                    mInteractor.deleteCommand(it)
                    viewState.showDeletedSnackbar(it)
                }
                .addTo(mDisposable)
    }

    override fun onUndoDelete(command: Command) =
            mInteractor.insertCommand(command)

    override fun exportCommands(fileName: String) {
        mInteractor.exportSchedule(fileName)
                .subscribe {
                    viewState.showInfoSnackbar(it)
                }
                .addTo(mDisposable)
    }

    override fun importCommands(fileName: String) {
        mInteractor.importSchedule(fileName)
                .subscribe {
                    viewState.showInfoSnackbar(it)
                }
                .addTo(mDisposable)
    }

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

    fun selectPeriod() = viewState.showSelectPeriodDialog(mSettings.schedulePeriod)

    fun onPeriodSelected(period: Int) {
        mSettings.schedulePeriod = period
        if (mSettings.useSchedule) {
            mAlarmInteractor.setAlarm()
                    .doOnError { viewState.showInfoToast(it.localizedMessage) }
                    .subscribe()
                    .addTo(mDisposable)
        }
    }

    fun updateNextRun() {
        val dateBuilder = DateBuilder(mSettings.scheduleTime)
        dateBuilder.period = mSettings.schedulePeriod
        viewState.showNextRun(dateBuilder.nextAlarmTime)
    }
}