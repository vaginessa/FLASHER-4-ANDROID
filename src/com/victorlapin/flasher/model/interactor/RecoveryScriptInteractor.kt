package com.victorlapin.flasher.model.interactor

import com.victorlapin.flasher.model.repository.RecoveryScriptRepository
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class RecoveryScriptInteractor constructor(
        private val mRepo: RecoveryScriptRepository
) {
    fun buildScript(): Single<String> = mRepo.buildScript()
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())

    fun deployScript(script: String) = mRepo.deployScript(script)
}