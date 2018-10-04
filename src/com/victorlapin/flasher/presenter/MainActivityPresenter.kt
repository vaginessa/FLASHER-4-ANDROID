package com.victorlapin.flasher.presenter

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.victorlapin.flasher.AboutScreen
import com.victorlapin.flasher.HomeScreen
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

    private fun selectHome() = mRouter.newRootScreen(HomeScreen())

    fun selectAbout() = mRouter.navigateTo(AboutScreen())
}