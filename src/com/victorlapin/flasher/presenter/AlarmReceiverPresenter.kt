package com.victorlapin.flasher.presenter

import com.victorlapin.flasher.manager.SettingsManager
import com.victorlapin.flasher.model.database.entity.Chain
import com.victorlapin.flasher.model.interactor.RecoveryScriptInteractor
import io.reactivex.disposables.Disposable

class AlarmReceiverPresenter(
        private val mSettings: SettingsManager,
        private val mScriptInteractor: RecoveryScriptInteractor
) {
    private var mDisposable: Disposable? = null

    fun buildAndDeploy() {
        mSettings.alarmLastRun = System.currentTimeMillis()
        mDisposable = mScriptInteractor.buildScript(Chain.SCHEDULE_ID)
                .subscribe({
                    val result = mScriptInteractor.deployScript(it.script)
                    if (result.isSuccess) {
                        mScriptInteractor.rebootRecovery()
                    }
                }, {
                    it.printStackTrace()
                })
    }
}