package com.victorlapin.flasher.work

import androidx.work.OneTimeWorkRequest
import androidx.work.Worker
import com.victorlapin.flasher.model.interactor.AlarmInteractor
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import timber.log.Timber
import java.util.concurrent.TimeUnit

class ScheduleStartWorker : Worker(), KoinComponent {
    private val mAlarmInteractor by inject<AlarmInteractor>()

    override fun doWork(): Result {
        Timber.i("One time worker started")
        mAlarmInteractor.setPeriodic().subscribe()
        Timber.i("One time worker finished")
        return Result.SUCCESS
    }

    override fun onStopped(cancelled: Boolean) {
        Timber.w("One time worker stopped")
    }

    companion object {
        const val JOB_TAG = "ScheduleStartWorker"

        fun buildRequest(nextRun: Long): OneTimeWorkRequest {
            val dateDiff = nextRun - System.currentTimeMillis()

            return OneTimeWorkRequest.Builder(ScheduleStartWorker::class.java)
                    .setInitialDelay(dateDiff, TimeUnit.MILLISECONDS)
                    .build()
        }
    }
}