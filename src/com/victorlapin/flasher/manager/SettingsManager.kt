package com.victorlapin.flasher.manager

import android.content.Context
import android.support.v7.preference.PreferenceManager
import com.victorlapin.flasher.R

class SettingsManager(context: Context) {
    companion object {
        const val KEY_THEME = "interface_theme"
        const val KEY_LAST_USED_PATH = "last_used_path"
        const val KEY_ABOUT = "open_about"
        const val KEY_DELETE_DEPLOYED_SCRIPT = "delete_deployed_script"
        const val KEY_USE_ANALYZER = "use_analyzer"
        const val KEY_SHOW_MASK_TOAST = "show_mask_toast"
        const val KEY_USE_SCHEDULE = "use_schedule"
        const val KEY_SCHEDULE_TIME = "schedule_time"
        const val KEY_SCHEDULE_INTERVAL = "schedule_interval"
        const val KEY_SCHEDULE_LAST_RUN = "alarm_last_run"
        const val KEY_CLEAR_SCHEDULE = "clear_schedule_settings"
        const val KEY_SHOW_NOTIFICATION_ON_BOOT = "show_notification_on_boot"
        const val KEY_BOOT_NOTIFICATION_FLAG = "boot_notification_flag"
        const val KEY_COMPRESS_BACKUPS = "compress_backups"
        const val KEY_DELETE_OLD_BACKUPS = "delete_old_backups"
        const val KEY_BACKUPS_TO_KEEP = "backups_to_keep"
        const val KEY_ENABLE_FILE_LOG = "enable_file_log"
        const val KEY_SCHEDULE_ONLY_CHARGING = "schedule_only_charging"
        const val KEY_SCHEDULE_ONLY_IDLE = "schedule_only_idle"
        const val KEY_SCHEDULE_ONLY_HIGH_BATTERY = "schedule_only_high_battery"
    }

    private val mPrefs = PreferenceManager.getDefaultSharedPreferences(context)

    val themeString: String
        get() = mPrefs.getString(KEY_THEME, R.style.AppTheme_Light.toString())!!

    val theme: Int
        get() = Integer.parseInt(themeString)

    var lastUsedPath: String?
        get() = mPrefs.getString(KEY_LAST_USED_PATH, null)
        set(path) = mPrefs.edit().putString(KEY_LAST_USED_PATH, path).apply()

    val useAnalyzer: Boolean
        get() = mPrefs.getBoolean(KEY_USE_ANALYZER, true)

    val showMaskToast: Boolean
        get() = mPrefs.getBoolean(KEY_SHOW_MASK_TOAST, false)

    var useSchedule: Boolean
        get() = mPrefs.getBoolean(KEY_USE_SCHEDULE, false)
        set(use) = mPrefs.edit().putBoolean(KEY_USE_SCHEDULE, use).apply()

    var scheduleTime: Long
        get() = mPrefs.getLong(KEY_SCHEDULE_TIME, 0)
        set(time) = mPrefs.edit().putLong(KEY_SCHEDULE_TIME, time).apply()

    var scheduleInterval: Int
        get() = mPrefs.getInt(KEY_SCHEDULE_INTERVAL, 0)
        set(interval) = mPrefs.edit().putInt(KEY_SCHEDULE_INTERVAL, interval).apply()

    var scheduleLastRun: Long
        get() = mPrefs.getLong(KEY_SCHEDULE_LAST_RUN, 0)
        set(time) = mPrefs.edit().putLong(KEY_SCHEDULE_LAST_RUN, time).apply()

    val showNotificationOnBoot: Boolean
        get() = mPrefs.getBoolean(KEY_SHOW_NOTIFICATION_ON_BOOT, false)

    var bootNotificationFlag: Boolean
        get() = mPrefs.getBoolean(KEY_BOOT_NOTIFICATION_FLAG, false)
        set(flag) = mPrefs.edit().putBoolean(KEY_BOOT_NOTIFICATION_FLAG, flag).apply()

    val compressBackups: Boolean
        get() = mPrefs.getBoolean(KEY_COMPRESS_BACKUPS, false)

    val deleteObsoleteBackups: Boolean
        get() = mPrefs.getBoolean(KEY_DELETE_OLD_BACKUPS, false)

    val backupsToKeep: Int
        get() = mPrefs.getString(KEY_BACKUPS_TO_KEEP, "2")!!.toInt()

    var enableFileLog: Boolean
        get() = mPrefs.getBoolean(KEY_ENABLE_FILE_LOG, false)
        set(value) = mPrefs.edit().putBoolean(KEY_ENABLE_FILE_LOG, value).apply()

    var scheduleOnlyCharging: Boolean
        get() = mPrefs.getBoolean(KEY_SCHEDULE_ONLY_CHARGING, false)
        set(value) = mPrefs.edit().putBoolean(KEY_SCHEDULE_ONLY_CHARGING, value).apply()

    var scheduleOnlyIdle: Boolean
        get() = mPrefs.getBoolean(KEY_SCHEDULE_ONLY_IDLE, false)
        set(value) = mPrefs.edit().putBoolean(KEY_SCHEDULE_ONLY_IDLE, value).apply()

    var scheduleOnlyHighBattery: Boolean
        get() = mPrefs.getBoolean(KEY_SCHEDULE_ONLY_HIGH_BATTERY, false)
        set(value) = mPrefs.edit().putBoolean(KEY_SCHEDULE_ONLY_HIGH_BATTERY, value).apply()
}