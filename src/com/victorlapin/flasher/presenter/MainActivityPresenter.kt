package com.victorlapin.flasher.presenter

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.victorlapin.flasher.AboutScreen
import com.victorlapin.flasher.HomeScreen
import com.victorlapin.flasher.manager.ServicesManager
import com.victorlapin.flasher.manager.SettingsManager
import com.victorlapin.flasher.view.MainActivityView
import ru.terrakok.cicerone.Router

@InjectViewState
class MainActivityPresenter constructor(
    private val mRouter: Router,
    private val mSettings: SettingsManager,
    private val mServices: ServicesManager
) : MvpPresenter<MainActivityView>() {
    private var mFingerprintChecked = false

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        selectHome()
    }

    override fun attachView(view: MainActivityView?) {
        super.attachView(view)
        if (mSettings.askFingerprintOnLaunch && !mFingerprintChecked &&
            mServices.isFingerprintAvailable()
        ) {
            viewState.askFingerprint()
        }
    }

    override fun detachView(view: MainActivityView?) {
        viewState.cancelFingerprint()
        super.detachView(view)
    }

    private fun selectHome() = mRouter.newRootScreen(HomeScreen())

    fun selectAbout() = mRouter.navigateTo(AboutScreen())

    fun exit() = mRouter.exit()

    fun onSuccessfulFingerprint() {
        mFingerprintChecked = true
    }
}