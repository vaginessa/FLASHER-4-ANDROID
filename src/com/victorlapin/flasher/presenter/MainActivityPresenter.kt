package com.victorlapin.flasher.presenter

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.victorlapin.flasher.Const
import com.victorlapin.flasher.view.MainActivityView
import ru.terrakok.cicerone.Router

@InjectViewState
class MainActivityPresenter constructor(
        private val mRouter: Router
): MvpPresenter<MainActivityView>() {
    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        selectHome()
    }

    private fun selectHome() = mRouter.newRootScreen(Const.FRAGMENT_HOME)

    fun selectAbout() = mRouter.navigateTo(Const.FRAGMENT_ABOUT)
}