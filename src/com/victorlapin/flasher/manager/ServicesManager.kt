package com.victorlapin.flasher.manager

import android.app.AlarmManager
import android.content.Context

class ServicesManager(
        private val mContext: Context
) {
    val alarmManager by lazy {
        mContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }
}