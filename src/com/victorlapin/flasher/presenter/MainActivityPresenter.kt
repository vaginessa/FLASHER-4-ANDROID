package com.victorlapin.flasher.presenter

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.victorlapin.flasher.Screens
import com.victorlapin.flasher.model.interactor.CommandsInteractor
import com.victorlapin.flasher.view.MainActivityView
import ru.terrakok.cicerone.Router

@InjectViewState
class MainActivityPresenter constructor(
        private val mRouter: Router,
        private val mCommandsInteractor: CommandsInteractor
): MvpPresenter<MainActivityView>() {
    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        selectHome()
    }

    fun selectHome() = mRouter.newRootScreen(Screens.FRAGMENT_HOME)

    fun selectSchedule() = mRouter.newRootScreen(Screens.FRAGMENT_SCHEDULE)

    fun selectSettings() = mRouter.newRootScreen(Screens.FRAGMENT_SETTINGS)

    fun onFabClicked() = mCommandsInteractor.addStubCommand()

    fun openAbout() = mRouter.navigateTo(Screens.ACTIVITY_ABOUT)
}