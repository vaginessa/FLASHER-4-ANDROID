package com.victorlapin.flasher.presenter

import com.arellomobile.mvp.InjectViewState
import com.victorlapin.flasher.addTo
import com.victorlapin.flasher.manager.SettingsManager
import com.victorlapin.flasher.model.database.entity.Command
import com.victorlapin.flasher.model.interactor.HomeInteractor
import com.victorlapin.flasher.model.interactor.RecoveryScriptInteractor
import com.victorlapin.flasher.view.HomeFragmentView
import io.reactivex.disposables.Disposable

@InjectViewState
class DefaultHomePresenter constructor(
        mScriptInteractor: RecoveryScriptInteractor,
        mSettings: SettingsManager,
        private val mInteractor: HomeInteractor
) : HomeFragmentPresenter(mScriptInteractor, mSettings) {
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

    override fun onCommandsDragged(fromId: Long, toId: Long) {
        var disposable: Disposable? = null
        disposable = mInteractor.getMovedCommands(fromId, toId)
                .subscribe({
                    val fromCommand = if (fromId < toId) it[0] else it[1]
                    val toCommand = if (toId < fromId) it[0] else it[1]
                    fromCommand.id = toId
                    toCommand.id = fromId

                    mInteractor.updateCommands(it)
                    disposable?.dispose()
                }, {
                    it.printStackTrace()
                })
    }
}