package com.victorlapin.flasher.manager

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v4.app.NotificationCompat
import com.victorlapin.flasher.R
import com.victorlapin.flasher.ui.receivers.AlarmBootReceiver
import com.victorlapin.flasher.ui.receivers.AlarmReceiver
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class ServicesManager(
        private val mContext: Context
) {
    val alarmManager by lazy {
        mContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    val alarmIntent: PendingIntent by lazy {
        val intent = Intent(mContext, AlarmReceiver::class.java)
        PendingIntent.getBroadcast(mContext, ALARM_REQUEST_CODE, intent,
                PendingIntent.FLAG_CANCEL_CURRENT)
    }

    fun enableBootReceiver() {
        val receiver = ComponentName(mContext, AlarmBootReceiver::class.java)
        val pm = mContext.packageManager
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP)
    }

    fun disableBootReceiver() {
        val receiver = ComponentName(mContext, AlarmBootReceiver::class.java)
        val pm = mContext.packageManager
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP)
    }

    val notificationManager by lazy {
        mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    fun showBootNotification(alarmLastRun: Long) {
        val formatter = SimpleDateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT)
        val notification = NotificationCompat.Builder(mContext,
                mContext.getString(R.string.channel_default_id))
                .setShowWhen(false)
                .setSmallIcon(R.drawable.android_head)
                .setContentText(mContext.getString(R.string.alarm_last_run_notification,
                        formatter.format(Date(alarmLastRun))))
                .build()
        notificationManager.notify(BOOT_NOTIFICATION_ID, notification)
    }

    companion object {
        private const val ALARM_REQUEST_CODE = 100
        private const val BOOT_NOTIFICATION_ID = 199
    }
}