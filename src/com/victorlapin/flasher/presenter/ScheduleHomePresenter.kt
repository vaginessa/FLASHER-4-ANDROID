package com.victorlapin.flasher.presenter

import com.arellomobile.mvp.InjectViewState
import com.victorlapin.flasher.manager.SettingsManager
import com.victorlapin.flasher.model.database.entity.Command
import com.victorlapin.flasher.model.interactor.RecoveryScriptInteractor
import com.victorlapin.flasher.model.interactor.ScheduleInteractor
import com.victorlapin.flasher.view.HomeFragmentView
import ru.terrakok.cicerone.Router

@InjectViewState
class ScheduleHomePresenter constructor(
        mScriptInteractor: RecoveryScriptInteractor,
        mSettings: SettingsManager,
        mRouter: Router,
        private val mInteractor: ScheduleInteractor
) : HomeFragmentPresenter(mRouter, mScriptInteractor, mSettings) {
    override fun attachView(view: HomeFragmentView?) {
        super.attachView(view)
        mDisposable = mInteractor.getSchedule()
                .subscribe {
                    viewState.setData(it, mFirstRun)
                    mFirstRun = false
                }
    }

    override fun detachView(view: HomeFragmentView?) {
        mDisposable?.dispose()
        mDisposable = null
        mFirstRun = true
        super.detachView(view)
    }

    override fun onCommandUpdated(command: Command) =
            mInteractor.updateCommand(command)

    override fun onCommandSwiped(id: Long) {
        mInteractor.getCommand(id)
                .subscribe {
                    mInteractor.deleteCommand(it)
                    viewState.showDeletedSnackbar(it)
                }
    }

    override fun onUndoDelete(command: Command) =
            mInteractor.insertCommand(command)

    override fun exportCommands(fileName: String) {
        mInteractor.exportSchedule(fileName)
                .subscribe {
                    viewState.showInfoSnackbar(it)
                }
    }

    override fun importCommands(fileName: String) {
        mInteractor.importSchedule(fileName)
                .subscribe {
                    viewState.showInfoSnackbar(it)
                }
    }
}