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
            Timber.plant(Timber.DebugTree())
        }
        if (mSettings.enableFileLog) {
            try {
                enableFileLogs()
            } catch (ex: Exception) {
                disableFileLogs()
                Timber.e(ex)
            }
        }
    }

    fun enableFileLogs() {
        Timber.plant(mTree)
    }

    fun disableFileLogs() {
        if (Timber.forest().contains(mTree)) {
            Timber.uproot(mTree)
        }
    }
}