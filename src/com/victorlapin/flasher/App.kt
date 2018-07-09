package com.victorlapin.flasher

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.victorlapin.flasher.di.allModules
import com.victorlapin.flasher.manager.ServicesManager
import org.koin.android.ext.android.inject
import org.koin.android.ext.android.startKoin
import org.koin.android.logger.AndroidLogger
import org.koin.log.EmptyLogger

class App : Application() {
    private val mServices by inject<ServicesManager>()

    override fun onCreate() {
        super.onCreate()
        val logger = if (BuildConfig.DEBUG) AndroidLogger() else EmptyLogger()
        startKoin(context = this, modules = allModules, logger = logger)
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = mServices.notificationManager

            val channelId = getString(R.string.channel_default_id)
            if (manager.getNotificationChannel(channelId) == null) {
                val channel = NotificationChannel(channelId,
                        getString(R.string.channel_default_title),
                        NotificationManager.IMPORTANCE_LOW)
                channel.setShowBadge(true)
                manager.createNotificationChannel(channel)
            }
        }
    }

}