package com.victorlapin.flasher.presenter

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.victorlapin.flasher.model.database.entity.Command
import com.victorlapin.flasher.model.interactor.CommandsInteractor
import com.victorlapin.flasher.view.HomeFragmentView
import io.reactivex.disposables.Disposable
import ru.terrakok.cicerone.Router

@InjectViewState
class HomeFragmentPresenter constructor(
        private val mRouter: Router,
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

    fun onCommandClicked(command: Command?) {

    }

    fun onCommandSwiped(id: Long) {
        mCommandsInteractor.getCommand(id)
                .subscribe {
                    mCommandsInteractor.deleteCommand(it)
                }
    }
}