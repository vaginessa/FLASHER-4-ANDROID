package com.victorlapin.flasher

import android.util.Log
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class FileTree : Timber.DebugTree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        val logFile = File(Const.LOG_FILENAME)
        if (!logFile.exists()) {
            logFile.parentFile.mkdirs()
            logFile.createNewFile()
        }
        val builder = StringBuilder()

        val dt = SimpleDateFormat("YYYY-MM-dd HH-mm-ss",
                Locale.getDefault()).format(Date())
        builder.append("$dt: ")

        when (priority) {
            Log.INFO -> builder.append("I/")
            Log.DEBUG -> builder.append("D/")
            Log.ERROR -> builder.append("E/")
            Log.WARN -> builder.append("W/")
        }
        builder.appendln("$tag: $message")
        t?.let {
            builder.appendln(it.toString())
        }

        FileOutputStream(logFile, true).use {
            it.write(builder.toString().toByteArray())
        }
    }
}