package com.victorlapin.flasher.presenter

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.victorlapin.flasher.Screens
import com.victorlapin.flasher.view.AboutActivityView
import ru.terrakok.cicerone.Router

@InjectViewState
class AboutActivityPresenter(private val mRouter: Router) :
        MvpPresenter<AboutActivityView>() {
    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        showFragment()
    }

    private fun showFragment() = mRouter.replaceScreen(Screens.FRAGMENT_ABOUT)

    fun onBackPressed() = mRouter.exit()
}