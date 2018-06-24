package com.victorlapin.flasher.presenter

import com.arellomobile.mvp.InjectViewState
import com.victorlapin.flasher.addTo
import com.victorlapin.flasher.manager.SettingsManager
import com.victorlapin.flasher.model.database.entity.Command
import com.victorlapin.flasher.model.interactor.CommandsInteractor
import com.victorlapin.flasher.model.interactor.RecoveryScriptInteractor
import com.victorlapin.flasher.view.HomeFragmentView
import ru.terrakok.cicerone.Router

@InjectViewState
class DefaultHomePresenter constructor(
        mScriptInteractor: RecoveryScriptInteractor,
        mSettings: SettingsManager,
        mRouter: Router,
        private val mInteractor: CommandsInteractor
) : HomeFragmentPresenter(mRouter, mScriptInteractor, mSettings) {
    override fun attachView(view: HomeFragmentView?) {
        super.attachView(view)
        mInteractor.getCommands()
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
        mInteractor.exportCommands(fileName)
                .subscribe {
                    viewState.showInfoSnackbar(it)
                }
                .addTo(mDisposable)
    }

    override fun importCommands(fileName: String) {
        mInteractor.importCommands(fileName)
                .subscribe {
                    viewState.showInfoSnackbar(it)
                }
                .addTo(mDisposable)
    }
}