package com.victorlapin.flasher.model.interactor

import com.victorlapin.flasher.model.BuildScriptResult
import com.victorlapin.flasher.model.repository.RecoveryScriptRepository
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class RecoveryScriptInteractor constructor(
        private val mRepo: RecoveryScriptRepository
) {
    fun buildScript(chainId: Long): Single<BuildScriptResult> = mRepo.buildScript(chainId)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())

    fun deployScript(script: String) = mRepo.deployScript(script)

    fun rebootRecovery() = mRepo.rebootRecovery()

    fun deleteScript() = mRepo.deleteScript()
}