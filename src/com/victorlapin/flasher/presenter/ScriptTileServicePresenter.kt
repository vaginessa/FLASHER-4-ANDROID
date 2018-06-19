package com.victorlapin.flasher.presenter

import com.victorlapin.flasher.model.database.entity.Chain
import com.victorlapin.flasher.model.interactor.RecoveryScriptInteractor
import com.victorlapin.flasher.view.ScriptTileServiceView

class ScriptTileServicePresenter constructor(
        private val mScriptInteractor: RecoveryScriptInteractor
) {
    private var mView: ScriptTileServiceView? = null

    fun setView(view: ScriptTileServiceView) {
        mView = view
    }

    fun buildAndDeploy() {
        mScriptInteractor.buildScript(Chain.DEFAULT_ID)
                .subscribe({
                    val result = mScriptInteractor.deployScript(it)
                    if (result.isSuccess) {
                        mView?.showRebootDialog()
                    } else {
                        mView?.showInfoToast(result)
                    }
                }, {
                    it.printStackTrace()
                })
    }
}