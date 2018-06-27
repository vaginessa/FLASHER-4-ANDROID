package com.victorlapin.flasher.presenter

import com.arellomobile.mvp.MvpPresenter
import com.victorlapin.flasher.addTo
import com.victorlapin.flasher.manager.SettingsManager
import com.victorlapin.flasher.model.CommandClickEventArgs
import com.victorlapin.flasher.model.database.entity.Command
import com.victorlapin.flasher.model.interactor.RecoveryScriptInteractor
import com.victorlapin.flasher.view.HomeFragmentView
import io.reactivex.disposables.CompositeDisposable
import java.io.File

abstract class HomeFragmentPresenter constructor(
        private val mScriptInteractor: RecoveryScriptInteractor,
        protected val mSettings: SettingsManager
) : MvpPresenter<HomeFragmentView>() {
    protected val mDisposable = CompositeDisposable()
    protected var mFirstRun = true

    override fun detachView(view: HomeFragmentView?) {
        mDisposable.clear()
        mFirstRun = true
        super.detachView(view)
    }

    abstract fun onCommandUpdated(command: Command)

    abstract fun onCommandSwiped(id: Long)

    abstract fun onUndoDelete(command: Command)

    fun onArgumentsClicked(args: CommandClickEventArgs) {
        when (args.argsType) {
            CommandClickEventArgs.ARG1 -> when (args.command.type) {
                Command.TYPE_WIPE -> viewState.showWipeDialog(args.command)
                Command.TYPE_BACKUP -> viewState.showBackupDialog(args.command)
                Command.TYPE_FLASH_FILE -> {
                    val path = when {
                        (args.command.arg2 != null) -> args.command.arg2
                        (mSettings.lastUsedPath != null) -> mSettings.lastUsedPath
                        else -> null
                    }
                    viewState.showFlashFileDialog(args.command, path)
                }
                Command.TYPE_FLASH_MASK -> viewState.showEditMaskDialog(args.command)
            }

            CommandClickEventArgs.ARG2 -> when (args.command.type) {
                Command.TYPE_FLASH_MASK -> {
                    val path = when {
                        (args.command.arg2 != null) -> args.command.arg2
                        (mSettings.lastUsedPath != null) -> mSettings.lastUsedPath
                        else -> null
                    }
                    viewState.showSelectFolderDialog(args.command, path)
                }
            }
        }
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
        mSettings.lastUsedPath = if (file.isDirectory) file.absolutePath else file.parent
    }

    fun buildAndDeploy(chainId: Long) {
        mScriptInteractor.buildScript(chainId)
                .subscribe({
                    if (mSettings.showMaskToast && it.resolvedFiles.isNotBlank()) {
                        viewState.showInfoToast(it.resolvedFiles)
                    }
                    val result = mScriptInteractor.deployScript(it.script)
                    if (result.isSuccess) {
                        viewState.showRebootSnackbar()
                    } else {
                        viewState.showInfoSnackbar(result)
                    }
                }, {
                    it.printStackTrace()
                })
                .addTo(mDisposable)
    }

    fun reboot() {
        val result = mScriptInteractor.rebootRecovery()
        if (!result.isSuccess) {
            viewState.showInfoSnackbar(result)
        }
    }

    fun onExportClicked() = viewState.showExportDialog()

    abstract fun exportCommands(fileName: String)

    fun onImportClicked() = viewState.showImportDialog()

    abstract fun importCommands(fileName: String)
}