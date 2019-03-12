package com.victorlapin.flasher.work

import android.content.Context
import androidx.work.Constraints
import androidx.work.OneTimeWorkRequest
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import com.victorlapin.flasher.Const
import com.victorlapin.flasher.R
import com.victorlapin.flasher.manager.ServicesManager
import com.victorlapin.flasher.manager.SettingsManager
import com.victorlapin.flasher.model.EventArgs
import com.victorlapin.flasher.model.database.entity.Chain
import com.victorlapin.flasher.model.interactor.RecoveryScriptInteractor
import io.reactivex.Completable
import io.reactivex.Single
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber
import java.util.concurrent.TimeUnit

class ScheduleWorker(context: Context, params: WorkerParameters) :
    RxWorker(context, params), KoinComponent {
    private val mSettings by inject<SettingsManager>()
    private val mScriptInteractor by inject<RecoveryScriptInteractor>()
    private val mServices by inject<ServicesManager>()

    override fun createWork(): Single<Result> = mScriptInteractor
        .buildScript(Chain.SCHEDULE_ID)
        .flatMap { mScriptInteractor.deployScript(it.script) }
        .flatMapCompletable { args ->
            if (args.isSuccess) {
                mSettings.bootNotificationFlag = true
                mScriptInteractor.rebootRecovery().subscribe()
            } else {
                mSettings.useSchedule = false
                mServices.showInfoNotification(args)
            }
            Completable.complete()
        }
        .toSingleDefault(Result.success())
        .onErrorReturnItem(Result.retry())
        .doOnSubscribe {
            Timber.i("Schedule worker started")
            getKoin().createScope(Const.WORKER_SCHEDULE)
        }
        .doOnError {
            Timber.e(it)
            if (it.message!! == "files must not be null") {
                mServices.showInfoNotification(
                    EventArgs(
                        isSuccess = false,
                        messageId = R.string.permission_denied_storage
                    )
                )
            } else {
                mServices.showInfoNotification(it.message)
            }
        }
        .doFinally {
            Timber.i("Schedule worker finished")
            getKoin().getScope(Const.WORKER_SCHEDULE).close()
        }

    companion object {
        const val JOB_TAG = "ScheduleWorker"

        fun buildRequest(nextRun: Long, settings: SettingsManager): OneTimeWorkRequest {
            val dateDiff = nextRun - System.currentTimeMillis()
            val constraints = Constraints.Builder()
                .setRequiresCharging(settings.scheduleOnlyCharging)
                .setRequiresDeviceIdle(settings.scheduleOnlyIdle)
                .setRequiresBatteryNotLow(settings.scheduleOnlyHighBattery)
                .build()

            return OneTimeWorkRequest.Builder(ScheduleWorker::class.java)
                .setInitialDelay(dateDiff, TimeUnit.MILLISECONDS)
                .setConstraints(constraints)
                .build()
        }
    }
}