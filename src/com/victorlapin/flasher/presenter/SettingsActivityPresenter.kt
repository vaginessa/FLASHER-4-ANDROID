package com.victorlapin.flasher.presenter

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.victorlapin.flasher.Screens
import com.victorlapin.flasher.view.SettingsActivityView
import ru.terrakok.cicerone.Router

@InjectViewState
class SettingsActivityPresenter(
        private val mRouter: Router
) : MvpPresenter<SettingsActivityView>() {
    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        mRouter.replaceScreen(Screens.FRAGMENT_SETTINGS)
    }

    fun onBackPressed() = mRouter.exit()

    fun openAbout() = mRouter.navigateTo(Screens.ACTIVITY_ABOUT)
}