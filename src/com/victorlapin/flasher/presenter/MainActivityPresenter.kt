package com.victorlapin.flasher.presenter

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.victorlapin.flasher.R
import com.victorlapin.flasher.Screens
import com.victorlapin.flasher.model.interactor.CommandsInteractor
import com.victorlapin.flasher.model.interactor.ScheduleInteractor
import com.victorlapin.flasher.view.MainActivityView
import ru.terrakok.cicerone.Router

@InjectViewState
class MainActivityPresenter constructor(
        private val mRouter: Router,
        private val mCommandsInteractor: CommandsInteractor,
        private val mScheduleInteractor: ScheduleInteractor
): MvpPresenter<MainActivityView>() {
    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        selectHome()
    }

    fun selectHome() = mRouter.newRootScreen(Screens.FRAGMENT_HOME)

    fun selectSchedule() = mRouter.newRootScreen(Screens.FRAGMENT_SCHEDULE)

    fun onFabClicked(currentFragmentId: Int) {
        when (currentFragmentId) {
            R.id.action_home -> mCommandsInteractor.addStubCommand()
            R.id.action_schedule -> mScheduleInteractor.addStubCommand()
        }
    }
}