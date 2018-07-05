package com.victorlapin.flasher.presenter

import com.arellomobile.mvp.InjectViewState
import com.victorlapin.flasher.manager.SettingsManager
import com.victorlapin.flasher.model.interactor.HomeInteractor
import com.victorlapin.flasher.model.interactor.RecoveryScriptInteractor

@InjectViewState
class DefaultHomePresenter constructor(
        mScriptInteractor: RecoveryScriptInteractor,
        mSettings: SettingsManager,
        mInteractor: HomeInteractor
) : BaseHomeFragmentPresenter(mScriptInteractor, mSettings, mInteractor)