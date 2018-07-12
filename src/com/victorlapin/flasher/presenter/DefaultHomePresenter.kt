package com.victorlapin.flasher.presenter

import com.arellomobile.mvp.InjectViewState
import com.victorlapin.flasher.R
import com.victorlapin.flasher.manager.SettingsManager
import com.victorlapin.flasher.model.interactor.HomeInteractor
import com.victorlapin.flasher.model.interactor.RecoveryScriptInteractor
import ru.terrakok.cicerone.Router

@InjectViewState
class DefaultHomePresenter constructor(
        router: Router,
        scriptInteractor: RecoveryScriptInteractor,
        settings: SettingsManager,
        interactor: HomeInteractor
) : BaseHomeFragmentPresenter(router, scriptInteractor, settings, interactor) {
    override val mCurrentFragmentId = R.id.action_home
}