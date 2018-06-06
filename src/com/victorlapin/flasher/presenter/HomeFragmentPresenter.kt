package com.victorlapin.flasher.presenter

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.victorlapin.flasher.model.database.entity.Command
import com.victorlapin.flasher.model.interactor.CommandsInteractor
import com.victorlapin.flasher.view.HomeFragmentView
import io.reactivex.disposables.Disposable

@InjectViewState
class HomeFragmentPresenter constructor(
        private val mCommandsInteractor: CommandsInteractor
) : MvpPresenter<HomeFragmentView>() {
    private var mDisposable: Disposable? = null

    override fun attachView(view: HomeFragmentView?) {
        super.attachView(view)
        mDisposable = mCommandsInteractor.getCommands()
                .subscribe {
                    viewState.setData(it)
                }
    }

    override fun detachView(view: HomeFragmentView?) {
        mDisposable?.dispose()
        mDisposable = null
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
        Command.TYPE_FLASH -> viewState.showFlashDialog(command)
        else -> Unit
    }
}