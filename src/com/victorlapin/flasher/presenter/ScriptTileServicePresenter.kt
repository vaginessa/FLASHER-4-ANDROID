package com.victorlapin.flasher.presenter

import com.victorlapin.flasher.manager.SettingsManager
import com.victorlapin.flasher.model.database.entity.Chain
import com.victorlapin.flasher.model.interactor.RecoveryScriptInteractor
import com.victorlapin.flasher.view.ScriptTileServiceView
import io.reactivex.disposables.Disposable
import timber.log.Timber

class ScriptTileServicePresenter constructor(
        private val mScriptInteractor: RecoveryScriptInteractor,
        private val mSettings: SettingsManager
) {
    private var mView: ScriptTileServiceView? = null
    private var mDisposable: Disposable? = null

    fun setView(view: ScriptTileServiceView) {
        mView = view
    }

    fun buildAndDeploy() {
        mDisposable = mScriptInteractor.buildScript(Chain.DEFAULT_ID)
                .subscribe({
                    if (mSettings.showMaskToast && it.resolvedFiles.isNotBlank()) {
                        mView?.showInfoToast(it.resolvedFiles)
                    }
                    val result = mScriptInteractor.deployScript(it.script)
                    if (result.isSuccess) {
                        mView?.showRebootDialog()
                    } else {
                        mView?.showInfoToast(result)
                    }
                }, {
                    Timber.e(it)
                })
    }

    fun cleanup() = mDisposable?.dispose()
}