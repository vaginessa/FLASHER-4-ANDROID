package com.victorlapin.flasher.manager

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import com.victorlapin.flasher.ui.receivers.AlarmBootReceiver
import com.victorlapin.flasher.ui.receivers.AlarmReceiver

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

    companion object {
        private const val ALARM_REQUEST_CODE = 100
    }
}