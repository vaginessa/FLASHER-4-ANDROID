package com.victorlapin.flasher.presenter

import com.arellomobile.mvp.MvpPresenter
import com.victorlapin.flasher.addTo
import com.victorlapin.flasher.manager.SettingsManager
import com.victorlapin.flasher.model.CommandClickEventArgs
import com.victorlapin.flasher.model.database.entity.Command
import com.victorlapin.flasher.model.interactor.BaseCommandsInteractor
import com.victorlapin.flasher.model.interactor.RecoveryScriptInteractor
import com.victorlapin.flasher.view.HomeFragmentView
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import java.io.File

abstract class BaseHomeFragmentPresenter constructor(
        private val mScriptInteractor: RecoveryScriptInteractor,
        protected val mSettings: SettingsManager,
        private val mInteractor: BaseCommandsInteractor
) : MvpPresenter<HomeFragmentView>() {
    protected val mDisposable = CompositeDisposable()
    private var mReorderSubject = PublishSubject.create<List<Command>>()

    override fun attachView(view: HomeFragmentView?) {
        super.attachView(view)
        mInteractor.getCommands()
                .subscribe {
                    viewState.setData(it)
                }
                .addTo(mDisposable)
        mReorderSubject
                .switchMap { commands ->
                    mInteractor.changeOrder(commands)
                }
                .subscribe { }
                .addTo(mDisposable)
    }

    override fun detachView(view: HomeFragmentView?) {
        mDisposable.clear()
        super.detachView(view)
    }

    fun onCommandUpdated(command: Command) =
            mInteractor.updateCommand(command)

    fun onCommandSwiped(id: Long) {
        mInteractor.getCommand(id)
                .subscribe {
                    mInteractor.deleteCommand(it)
                    viewState.showDeletedSnackbar(it)
                }
                .addTo(mDisposable)
    }

    fun onUndoDelete(command: Command) =
            mInteractor.insertCommand(command)

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
        viewState.toggleProgress(true)
        mScriptInteractor.buildScript(chainId)
                .subscribe({
                    if (mSettings.showMaskToast && it.resolvedFiles.isNotBlank()) {
                        viewState.showInfoToast(it.resolvedFiles)
                    }
                    val result = mScriptInteractor.deployScript(it.script)
                    viewState.toggleProgress(false)
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
        viewState.toggleProgress(true)
        val result = mScriptInteractor.rebootRecovery()
        if (!result.isSuccess) {
            viewState.toggleProgress(false)
            viewState.showInfoSnackbar(result)
        }
    }

    fun onExportClicked() = viewState.showExportDialog()

    fun exportCommands(fileName: String) {
        mInteractor.exportCommands(fileName)
                .subscribe {
                    viewState.showInfoSnackbar(it)
                }
                .addTo(mDisposable)
    }

    fun onImportClicked() = viewState.showImportDialog()

    fun importCommands(fileName: String) {
        mInteractor.importCommands(fileName)
                .subscribe {
                    viewState.showInfoSnackbar(it)
                }
                .addTo(mDisposable)
    }

    fun onOrderChanged(commands: List<Command>) = mReorderSubject.onNext(commands)
}