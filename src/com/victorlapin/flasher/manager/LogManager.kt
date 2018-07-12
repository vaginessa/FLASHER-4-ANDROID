package com.victorlapin.flasher.manager

import com.victorlapin.flasher.BuildConfig
import com.victorlapin.flasher.FileTree
import timber.log.Timber

class LogManager(
        private val mSettings: SettingsManager
) {
    private val mTag = LogManager::class.java.simpleName
    private val mTree = FileTree()

    fun onStartup() {
        if (BuildConfig.DEBUG) {
            Timber.tag(mTag).i("Initializing DebugTree")
            Timber.plant(Timber.DebugTree())
            Timber.tag(mTag).i("DebugTree planted")
        }
        if (mSettings.enableFileLog) {
            try {
                Timber.tag(mTag).i("Initializing FileTree")
                enableFileLogs()
            } catch (ex: Exception) {
                disableFileLogs()
                Timber.tag(mTag).e(ex)
            }
        }
    }

    fun enableFileLogs() {
        Timber.plant(mTree)
        Timber.tag(mTag).i("FileTree planted")
    }

    fun disableFileLogs() {
        if (Timber.forest().contains(mTree)) {
            Timber.uproot(mTree)
            Timber.tag(mTag).i("FileTree uprooted")
        }
    }
}