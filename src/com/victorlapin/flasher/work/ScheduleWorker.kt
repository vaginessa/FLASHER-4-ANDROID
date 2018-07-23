package com.victorlapin.flasher.work

import androidx.work.Constraints
import androidx.work.PeriodicWorkRequest
import androidx.work.Worker
import com.victorlapin.flasher.manager.ServicesManager
import com.victorlapin.flasher.manager.SettingsManager
import com.victorlapin.flasher.model.database.entity.Chain
import com.victorlapin.flasher.model.interactor.RecoveryScriptInteractor
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import timber.log.Timber
import java.util.concurrent.TimeUnit

class ScheduleWorker : Worker(), KoinComponent {
    private val mSettings by inject<SettingsManager>()
    private val mScriptInteractor by inject<RecoveryScriptInteractor>()
    private val mServices by inject<ServicesManager>()

    override fun doWork(): Result {
        Timber.i("Periodic worker started")
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
            Timber.i("Periodic worker finished")
            Result.SUCCESS
        } catch (t: Throwable) {
            Timber.e(t)
            mServices.showInfoNotification(t.message)
            Result.RETRY
        }
    }

    override fun onStopped(cancelled: Boolean) {
        Timber.w("Periodic worker stopped")
    }

    companion object {
        const val JOB_TAG = "ScheduleWorker"

        fun buildRequest(settings: SettingsManager): PeriodicWorkRequest {
            val constraints = Constraints.Builder()
                    .setRequiresCharging(settings.scheduleOnlyCharging)
                    .setRequiresDeviceIdle(settings.scheduleOnlyIdle)
                    .setRequiresBatteryNotLow(settings.scheduleOnlyHighBattery)
                    .build()

            return PeriodicWorkRequest.Builder(ScheduleWorker::class.java,
                    settings.scheduleInterval.toLong(), TimeUnit.DAYS)
                    .setConstraints(constraints)
                    .build()
        }
    }
}