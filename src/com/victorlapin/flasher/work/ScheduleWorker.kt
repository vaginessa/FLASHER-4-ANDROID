package com.victorlapin.flasher.work

import android.content.Context
import androidx.work.Constraints
import androidx.work.OneTimeWorkRequest
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.victorlapin.flasher.Const
import com.victorlapin.flasher.manager.ServicesManager
import com.victorlapin.flasher.manager.SettingsManager
import com.victorlapin.flasher.model.database.entity.Chain
import com.victorlapin.flasher.model.interactor.RecoveryScriptInteractor
import org.koin.standalone.KoinComponent
import org.koin.standalone.getKoin
import org.koin.standalone.inject
import timber.log.Timber
import java.util.concurrent.TimeUnit

class ScheduleWorker(context: Context, params: WorkerParameters) :
        Worker(context, params), KoinComponent {
    private val mSettings by inject<SettingsManager>()
    private val mScriptInteractor by inject<RecoveryScriptInteractor>()
    private val mServices by inject<ServicesManager>()

    override fun doWork(): Result {
        val scope = getKoin().createScope(Const.WORKER_SCHEDULE)
        Timber.i("Schedule worker started")
        mSettings.scheduleLastRun = System.currentTimeMillis()
        return try {
            val scriptResult = mScriptInteractor
                    .buildScript(Chain.SCHEDULE_ID)
                    .blockingGet()
            val result = mScriptInteractor.deployScript(scriptResult.script)
            if (result.isSuccess) {
                mSettings.bootNotificationFlag = true
                mScriptInteractor.rebootRecovery().subscribe()
            } else {
                mSettings.useSchedule = false
                mServices.showInfoNotification(result)
            }
            Timber.i("Schedule worker finished")
            Result.SUCCESS
        } catch (t: Throwable) {
            Timber.e(t)
            mServices.showInfoNotification(t.message)
            Result.RETRY
        } finally {
            scope.close()
        }
    }

    override fun onStopped() {
        Timber.w("Schedule worker stopped")
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