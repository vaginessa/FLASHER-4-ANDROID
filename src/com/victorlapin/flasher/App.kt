package com.victorlapin.flasher

import android.app.Application
import com.victorlapin.flasher.di.allModules
import org.koin.android.ext.android.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin(this, allModules)
    }
}