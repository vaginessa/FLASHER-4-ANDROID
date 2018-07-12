package com.victorlapin.flasher

import android.os.Environment
import java.io.File

object Const {
    val APP_FOLDER: String = File(Environment.getExternalStorageDirectory(),
            "Flasher").absolutePath

    val TWRP_FOLDER: String = File(Environment.getExternalStorageDirectory(),
            "TWRP").absolutePath

    const val SCRIPT_FILENAME: String = "/cache/recovery/openrecoveryscript"

    val DEBUG_FILENAME: String = File(APP_FOLDER, "openrecoveryscript").absolutePath

    val LOG_FILENAME: String = File(APP_FOLDER, "app.log").absolutePath
}