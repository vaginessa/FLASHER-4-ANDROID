package com.victorlapin.flasher.presenter

import com.arellomobile.mvp.MvpPresenter
import com.victorlapin.flasher.R
import com.victorlapin.flasher.Screens
import com.victorlapin.flasher.manager.SettingsManager
import com.victorlapin.flasher.model.CommandClickEventArgs
import com.victorlapin.flasher.model.database.entity.Command
import com.victorlapin.flasher.model.interactor.BaseCommandsInteractor
import com.victorlapin.flasher.model.interactor.RecoveryScriptInteractor
import com.victorlapin.flasher.view.HomeFragmentView
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import ru.terrakok.cicerone.Router
import timber.log.Timber
import java.io.File

abstract class BaseHomeFragmentPresenter constructor(
        private val mRouter: Router,
        private val mScriptInteractor: RecoveryScriptInteractor,
        protected val mSettings: SettingsManager,
        private val mInteractor: BaseCommandsInteractor
) : MvpPresenter<HomeFragmentView>() {
    private var mDisposable: Disposable? = null
    private var mReorderSubject = PublishSubject.create<List<Command>>()
    protected abstract val mCurrentFragmentId: Int

    override fun attachView(view: HomeFragmentView?) {
        super.attachView(view)
        mDisposable = mInteractor.getCommands()
                .subscribe {
                    viewState.setData(it)
                }
        mReorderSubject
                .switchMapCompletable { commands ->
                    mInteractor.changeOrder(commands)
                }
                .subscribe()
    }

    override fun detachView(view: HomeFragmentView?) {
        mDisposable?.dispose()
        super.detachView(view)
    }

    fun onCommandUpdated(command: Command) =
            mInteractor.updateCommand(command)

    fun onCommandSwiped(id: Long) {
        mInteractor.getCommand(id)
                .doOnSuccess {
                    mInteractor.deleteCommand(it)
                    viewState.showDeletedSnackbar(it)
                }
                .subscribe()
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
        mScriptInteractor.buildScript(chainId)
                .doOnSubscribe { viewState.toggleProgress(true) }
                .doOnSuccess {
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
                }
                .doOnError { Timber.e(it) }
                .subscribe()
    }

    fun reboot() {
        mScriptInteractor.rebootRecovery()
                .doOnSubscribe { viewState.toggleProgress(true) }
                .doOnSuccess {
                    if (!it.isSuccess) {
                        viewState.toggleProgress(false)
                        viewState.showInfoSnackbar(it)
                    }
                }
                .subscribe()
    }

    fun onExportClicked() = viewState.showExportDialog()

    fun exportCommands(fileName: String) {
        mInteractor.exportCommands(fileName)
                .doOnSuccess { viewState.showInfoSnackbar(it) }
                .doOnError { Timber.e(it) }
                .subscribe()
    }

    fun onImportClicked() = viewState.showImportDialog()

    fun importCommands(fileName: String) {
        mInteractor.importCommands(fileName)
                .doOnSuccess { viewState.showInfoSnackbar(it) }
                .doOnError { Timber.e(it) }
                .subscribe()
    }

    fun onOrderChanged(commands: List<Command>) = mReorderSubject.onNext(commands)

    private fun selectHome() = mRouter.newRootScreen(Screens.FRAGMENT_HOME)

    private fun selectSchedule() = mRouter.newRootScreen(Screens.FRAGMENT_SCHEDULE)

    fun onFabClicked() = mInteractor.addStubCommand()

    fun selectSettings() = mRouter.navigateTo(Screens.FRAGMENT_SETTINGS)

    fun selectNavigation() = viewState.showNavigationFragment(mCurrentFragmentId)

    fun onNavigationClicked(selectedId: Int) {
        if (selectedId != mCurrentFragmentId) {
            when (selectedId) {
                R.id.action_home -> selectHome()
                R.id.action_schedule -> selectSchedule()
            }
        }
    }
}