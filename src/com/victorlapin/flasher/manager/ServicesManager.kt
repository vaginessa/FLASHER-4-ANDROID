package com.victorlapin.flasher.manager

import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.hardware.fingerprint.FingerprintManagerCompat
import com.victorlapin.flasher.R
import com.victorlapin.flasher.model.EventArgs
import com.victorlapin.flasher.ui.receivers.BootReceiver
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class ServicesManager(
        private val mContext: Context
) {
    val context = mContext

    fun enableBootReceiver() {
        val receiver = ComponentName(mContext, BootReceiver::class.java)
        val pm = mContext.packageManager
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP)
    }

    fun disableBootReceiver() {
        val receiver = ComponentName(mContext, BootReceiver::class.java)
        val pm = mContext.packageManager
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP)
    }

    val notificationManager by lazy {
        mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    fun showBootNotification(lastRun: Long) {
        val formatter = SimpleDateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT)
        val notification = NotificationCompat.Builder(mContext,
                mContext.getString(R.string.channel_default_id))
                .setShowWhen(true)
                .setSmallIcon(R.drawable.android_head)
                .setContentText(mContext.getString(R.string.schedule_last_run_notification,
                        formatter.format(Date(lastRun))))
                .build()
        notificationManager.notify(BOOT_NOTIFICATION_ID, notification)
    }

    fun showInfoNotification(eventArgs: EventArgs) {
        val text = when {
            eventArgs.message != null -> eventArgs.message
            eventArgs.messageId != null -> mContext.getString(eventArgs.messageId)
            else -> null
        }
        showInfoNotification(text)
    }

    fun showInfoNotification(message: String?) {
        message?.let {
            val notification = NotificationCompat.Builder(mContext,
                    mContext.getString(R.string.channel_default_id))
                    .setShowWhen(true)
                    .setSmallIcon(R.drawable.android_head)
                    .setContentText(mContext.getString(R.string.schedule_error_notification, it))
                    .build()
            notificationManager.notify(INFO_NOTIFICATION_ID, notification)
        }
    }

    val selfPackageName: String = mContext.packageName

    val packageManager: PackageManager by lazy {
        mContext.packageManager
    }

    private val fingerprintManager by lazy {
        FingerprintManagerCompat.from(mContext)
    }

    fun isFingerprintAvailable(): Boolean =
            fingerprintManager.isHardwareDetected
                    && fingerprintManager.hasEnrolledFingerprints()

    companion object {
        private const val BOOT_NOTIFICATION_ID = 199
        private const val INFO_NOTIFICATION_ID = 200
    }
}