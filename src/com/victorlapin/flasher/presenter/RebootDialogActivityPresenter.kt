package com.victorlapin.flasher.presenter

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.victorlapin.flasher.model.interactor.RecoveryScriptInteractor
import com.victorlapin.flasher.view.RebootDialogActivityView

@InjectViewState
class RebootDialogActivityPresenter constructor(
        private val mScriptInteractor: RecoveryScriptInteractor
) : MvpPresenter<RebootDialogActivityView>() {
    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.showRebootDialog()
    }

    fun rebootRecovery() {
        mScriptInteractor.rebootRecovery().subscribe()
    }
}