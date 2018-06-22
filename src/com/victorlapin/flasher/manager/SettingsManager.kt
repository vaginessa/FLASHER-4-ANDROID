package com.victorlapin.flasher.manager

import android.content.Context
import android.support.v7.preference.PreferenceManager
import com.victorlapin.flasher.R

class SettingsManager(context: Context) {
    companion object {
        const val KEY_THEME = "interface_theme"
        const val KEY_LAST_USED_PATH = "last_used_path"
        const val KEY_ABOUT = "open_about"
        const val KEY_SAVE_DEBUG_SCRIPT = "save_debug_script"
        const val KEY_DELETE_DEPLOYED_SCRIPT = "delete_deployed_script"
        const val KEY_USE_ANALYZER = "use_analyzer"
        const val KEY_SHOW_MASK_TOAST = "show_mask_toast"
        const val KEY_USE_SCHEDULE = "use_schedule"
        const val KEY_SCHEDULE_TIME = "schedule_time"
        const val KEY_SCHEDULE_PERIOD = "schedule_period"
    }

    private val mPrefs = PreferenceManager.getDefaultSharedPreferences(context)

    val themeString: String
        get() = mPrefs.getString(KEY_THEME, R.style.AppTheme_Light.toString())

    val theme: Int
        get() = Integer.parseInt(themeString)

    var lastUsedPath: String?
        get() = mPrefs.getString(KEY_LAST_USED_PATH, null)
        set(path) = mPrefs.edit().putString(KEY_LAST_USED_PATH, path).apply()

    val saveDebugScript: Boolean
        get() = mPrefs.getBoolean(KEY_SAVE_DEBUG_SCRIPT, false)

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

    var schedulePeriod: Int
        get() = mPrefs.getInt(KEY_SCHEDULE_PERIOD, 0)
        set(period) = mPrefs.edit().putInt(KEY_SCHEDULE_PERIOD, period).apply()
}