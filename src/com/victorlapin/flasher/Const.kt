package com.victorlapin.flasher

import android.os.Environment
import java.io.File

object Const {
    val APP_FOLDER: String = File(Environment.getExternalStorageDirectory(),
            "Flasher").absolutePath

    val TWRP_FOLDER: String = File(Environment.getExternalStorageDirectory(),
            "TWRP").absolutePath
}