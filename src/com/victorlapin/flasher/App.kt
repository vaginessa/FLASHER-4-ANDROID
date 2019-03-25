package com.victorlapin.flasher

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.topjohnwu.superuser.Shell
import com.victorlapin.flasher.di.allModules
import com.victorlapin.flasher.manager.LogManager
import com.victorlapin.flasher.manager.ServicesManager
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.logger.AndroidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.EmptyLogger
import org.koin.core.logger.Level

class App : Application() {
    private val mServices by inject<ServicesManager>()
    private val mLogs by inject<LogManager>()

    init {
        Shell.Config.verboseLogging(BuildConfig.DEBUG)
    }

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            logger(if (BuildConfig.DEBUG) AndroidLogger(Level.DEBUG) else EmptyLogger())
            modules(allModules)
        }
        createNotificationChannels()
        mLogs.onStartup()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = mServices.notificationManager

            val channelId = getString(R.string.channel_default_id)
            if (manager.getNotificationChannel(channelId) == null) {
                val channel = NotificationChannel(
                    channelId,
                    getString(R.string.channel_default_title),
                    NotificationManager.IMPORTANCE_LOW
                )
                channel.setShowBadge(true)
                manager.createNotificationChannel(channel)
            }
        }
    }

}