package com.victorlapin.flasher.presenter

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.victorlapin.flasher.manager.SettingsManager
import com.victorlapin.flasher.model.interactor.RecoveryScriptInteractor
import com.victorlapin.flasher.view.RebootDialogActivityView

@InjectViewState
class RebootDialogActivityPresenter constructor(
        private val mScriptInteractor: RecoveryScriptInteractor,
        private val mSettings: SettingsManager
) : MvpPresenter<RebootDialogActivityView>() {
    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.showRebootDialog()
    }

    fun onRebootRequested() {
        if (mSettings.askFingerprintToReboot) {
            viewState.askFingerprint()
        } else {
            rebootRecovery()
        }
    }

    fun rebootRecovery() {
        mScriptInteractor.rebootRecovery().subscribe()
    }
}