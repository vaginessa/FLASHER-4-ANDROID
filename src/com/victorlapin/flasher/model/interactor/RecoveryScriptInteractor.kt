package com.victorlapin.flasher.model.interactor

import com.victorlapin.flasher.manager.SettingsManager
import com.victorlapin.flasher.model.BuildScriptResult
import com.victorlapin.flasher.model.EventArgs
import com.victorlapin.flasher.model.repository.BackupsRepository
import com.victorlapin.flasher.model.repository.CommandsRepository
import com.victorlapin.flasher.model.repository.RecoveryScriptRepository
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class RecoveryScriptInteractor constructor(
        private val mScriptRepo: RecoveryScriptRepository,
        private val mCommandsRepo: CommandsRepository,
        private val mBackupsRepo: BackupsRepository,
        private val mSettings: SettingsManager
) {
    fun buildScript(chainId: Long): Single<BuildScriptResult> =
            Single.create<BuildScriptResult> { emitter ->
                var disposable: Disposable? = null
                disposable = mCommandsRepo.getCommands(chainId)
                        .subscribe {
                            val result = mScriptRepo.generateScript(it, mSettings.compressBackups)
                            emitter.onSuccess(result)
                            disposable?.dispose()
                        }
            }
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())

    fun deployScript(script: String): EventArgs {
        if (mSettings.useAnalyzer) {
            val analyzeResult = mScriptRepo.analyzeScript(script)
            if (analyzeResult != null) {
                Timber.i("Analyzer checks failed, script won't be deployed")
                return analyzeResult
            }
        }
        val result = mScriptRepo.deployScript(script)
        if (result.isSuccess && script.contains("backup ")
                && mSettings.deleteObsoleteBackups) {
            var backupsToKeep = mSettings.backupsToKeep
            if (backupsToKeep <= 0) backupsToKeep = 1
            mBackupsRepo.deleteObsoleteBackups(backupsToKeep)
        }
        return result
    }

    fun rebootRecovery() = mScriptRepo.rebootRecovery()

    fun deleteScript() = mScriptRepo.deleteScript()
}