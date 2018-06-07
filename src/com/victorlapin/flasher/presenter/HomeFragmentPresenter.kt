package com.victorlapin.flasher.presenter

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.victorlapin.flasher.manager.SettingsManager
import com.victorlapin.flasher.model.database.entity.Command
import com.victorlapin.flasher.model.interactor.CommandsInteractor
import com.victorlapin.flasher.view.HomeFragmentView
import io.reactivex.disposables.Disposable
import java.io.File

@InjectViewState
class HomeFragmentPresenter constructor(
        private val mCommandsInteractor: CommandsInteractor,
        private val mSettings: SettingsManager
) : MvpPresenter<HomeFragmentView>() {
    private var mDisposable: Disposable? = null
    private var mFirstRun = true

    override fun attachView(view: HomeFragmentView?) {
        super.attachView(view)
        mDisposable = mCommandsInteractor.getCommands()
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

    fun onCommandUpdated(command: Command) =
            mCommandsInteractor.updateCommand(command)

    fun onCommandSwiped(id: Long) {
        mCommandsInteractor.getCommand(id)
                .subscribe {
                    mCommandsInteractor.deleteCommand(it)
                }
    }

    fun onArgumentsClicked(command: Command) = when (command.type) {
        Command.TYPE_WIPE -> viewState.showWipeDialog(command)
        Command.TYPE_BACKUP -> viewState.showBackupDialog(command)
        Command.TYPE_FLASH -> {
            val path = when {
                (command.arg2 != null) -> command.arg2
                (mSettings.lastUsedPath != null) -> mSettings.lastUsedPath
                else -> null
            }
            viewState.showFlashDialog(command, path)
        }
        else -> Unit
    }

    fun onCommandTypeChanged(pair: Pair<Command, Int>) {
        val command = pair.first
        val newType = pair.second
        if (command.type != newType) {
            command.type = newType
            command.arg1 = null
            command.arg2 = null
            onCommandUpdated(command)
        }
    }

    fun onFileSelected(file: File) {
        mSettings.lastUsedPath = file.parent
    }
}