package com.victorlapin.flasher.manager

import com.victorlapin.flasher.BuildConfig
import com.victorlapin.flasher.FileTree
import timber.log.Timber

class LogManager(
        private val mSettings: SettingsManager
) {
    private val mTree = FileTree()

    fun onStartup() {
        if (BuildConfig.DEBUG) {
            Timber.i("Initializing DebugTree")
            Timber.plant(Timber.DebugTree())
            Timber.i("DebugTree planted")
        }
        if (mSettings.enableFileLog) {
            try {
                Timber.i("Initializing FileTree")
                enableFileLogs()
            } catch (ex: Exception) {
                disableFileLogs()
                Timber.e(ex)
            }
        }
    }

    fun enableFileLogs() {
        Timber.plant(mTree)
        Timber.i("FileTree planted")
    }

    fun disableFileLogs() {
        if (Timber.forest().contains(mTree)) {
            Timber.uproot(mTree)
            Timber.i("FileTree uprooted")
        }
    }
}