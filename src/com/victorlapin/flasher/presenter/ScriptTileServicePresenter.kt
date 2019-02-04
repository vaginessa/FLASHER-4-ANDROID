package com.victorlapin.flasher.presenter

import com.victorlapin.flasher.R
import com.victorlapin.flasher.manager.SettingsManager
import com.victorlapin.flasher.model.EventArgs
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
            .flatMap {
                if (mSettings.showMaskToast && it.resolvedFiles.isNotBlank()) {
                    mView?.showInfoToast(it.resolvedFiles)
                }
                mScriptInteractor.deployScript(it.script)
            }
            .subscribe({
                if (it.isSuccess) {
                    mView?.showRebootDialog()
                } else {
                    mView?.showInfoToast(it)
                }
            }, {
                Timber.e(it)
                if (it.message!! == "files must not be null") {
                    mView?.showInfoToast(
                        EventArgs(
                            isSuccess = false,
                            messageId = R.string.permission_denied_storage
                        )
                    )
                } else {
                    mView?.showInfoToast(it.message!!)
                }
            })
    }

    fun cleanup() = mDisposable?.dispose()
}