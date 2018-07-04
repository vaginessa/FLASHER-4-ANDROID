package com.victorlapin.flasher.presenter

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.victorlapin.flasher.R
import com.victorlapin.flasher.Screens
import com.victorlapin.flasher.model.interactor.HomeInteractor
import com.victorlapin.flasher.model.interactor.ScheduleInteractor
import com.victorlapin.flasher.view.MainActivityView
import ru.terrakok.cicerone.Router

@InjectViewState
class MainActivityPresenter constructor(
        private val mRouter: Router,
        private val mHomeInteractor: HomeInteractor,
        private val mScheduleInteractor: ScheduleInteractor
): MvpPresenter<MainActivityView>() {
    private var mCurrentFragmentId: Int = R.id.action_home

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        selectHome()
    }

    private fun selectHome() = mRouter.newRootScreen(Screens.FRAGMENT_HOME)

    private fun selectSchedule() = mRouter.newRootScreen(Screens.FRAGMENT_SCHEDULE)

    fun onFabClicked() {
        when (mCurrentFragmentId) {
            R.id.action_home -> mHomeInteractor.addStubCommand()
            R.id.action_schedule -> mScheduleInteractor.addStubCommand()
        }
    }

    fun selectSettings() = mRouter.navigateTo(Screens.ACTIVITY_SETTINGS)

    fun selectNavigation() = viewState.showNavigationFragment(mCurrentFragmentId)

    fun onNavigationClicked(selectedId: Int) {
        if (selectedId != mCurrentFragmentId) {
            mCurrentFragmentId = selectedId
            when (mCurrentFragmentId) {
                R.id.action_home -> selectHome()
                R.id.action_schedule -> selectSchedule()
            }
        }
    }
}