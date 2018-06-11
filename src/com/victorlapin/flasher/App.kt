package com.victorlapin.flasher

import android.app.Application
import com.victorlapin.flasher.di.allModules
import org.koin.android.ext.android.startKoin
import org.koin.android.logger.AndroidLogger
import org.koin.log.EmptyLogger

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        val logger = if (BuildConfig.DEBUG) AndroidLogger() else EmptyLogger()
        startKoin(application = this, modules = allModules, logger = logger)
    }
}