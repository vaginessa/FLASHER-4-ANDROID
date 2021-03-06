package com.victorlapin.flasher.model.interactor

import com.victorlapin.flasher.manager.ServicesManager
import com.victorlapin.flasher.manager.SettingsManager
import com.victorlapin.flasher.model.BuildScriptResult
import com.victorlapin.flasher.model.EventArgs
import com.victorlapin.flasher.model.repository.BackupsRepository
import com.victorlapin.flasher.model.repository.CommandsRepository
import com.victorlapin.flasher.model.repository.RecoveryScriptRepository
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class RecoveryScriptInteractor constructor(
    private val mScriptRepo: RecoveryScriptRepository,
    private val mCommandsRepo: CommandsRepository,
    private val mBackupsRepo: BackupsRepository,
    private val mSettings: SettingsManager,
    private val mServices: ServicesManager
) {
    fun buildScript(chainId: Long): Single<BuildScriptResult> =
        mCommandsRepo.getCommands(chainId)
            .firstOrError()
            .map {
                mScriptRepo.generateScript(it, mSettings.compressBackups)
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())

    fun deployScript(script: String): Single<EventArgs> = Single.create<EventArgs> { emitter ->
        if (mSettings.useAnalyzer) {
            val analyzeResult = mScriptRepo.analyzeScript(script)
            analyzeResult?.let {
                Timber.i("Analyzer checks failed, script won't be deployed")
                emitter.onSuccess(it)
            }
        }
        val result = mScriptRepo.deployScript(script)
        if (result.isSuccess && script.contains("backup ")
            && mSettings.deleteObsoleteBackups
            && mSettings.backupsPath != null
        ) {
            var backupsToKeep = mSettings.backupsToKeep
            if (backupsToKeep <= 0) backupsToKeep = 1
            mBackupsRepo.deleteObsoleteBackups(
                mServices.context,
                mSettings.backupsPath, backupsToKeep
            )
        }
        emitter.onSuccess(result)
    }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

    fun rebootRecovery(): Single<EventArgs> = Single.create<EventArgs> { emitter ->
        val result = mScriptRepo.rebootRecovery()
        emitter.onSuccess(result)
    }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

    fun deleteScript(): Completable = Completable.create { emitter ->
        mScriptRepo.deleteScript()
        emitter.onComplete()
    }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
}