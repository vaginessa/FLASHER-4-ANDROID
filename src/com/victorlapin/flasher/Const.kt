package com.victorlapin.flasher

import android.os.Environment
import java.io.File

object Const {
    const val ACTIVITY_MAIN = "activity_main"
    const val ACTIVITY_REBOOT_DIALOG = "activity_reboot_dialog"

    const val FRAGMENT_HOME = "fragment_home"
    const val FRAGMENT_SCHEDULE = "fragment_schedule"
    const val FRAGMENT_SETTINGS = "fragment_settings"
    const val FRAGMENT_ABOUT = "fragment_about"
    const val FRAGMENT_BOTTOM = "fragment_bottom"

    const val EXTERNAL_ABOUT = "about"

    const val SERVICE_SCRIPT = "tile_service_script"

    const val RECEIVER_BOOT = "receiver_boot"

    const val WORKER_SCHEDULE = "worker_schedule"


    val APP_FOLDER: String = File(Environment.getExternalStorageDirectory(),
            "Flasher").absolutePath

    val TWRP_FOLDER: String = File(Environment.getExternalStorageDirectory(),
            "TWRP").absolutePath

    const val SCRIPT_FILENAME: String = "/cache/recovery/openrecoveryscript"

    val LOG_FILENAME: String = File(APP_FOLDER, "app.log").absolutePath

    val FALLBACK_FOLDER: String = Environment.getExternalStorageDirectory().absolutePath
}