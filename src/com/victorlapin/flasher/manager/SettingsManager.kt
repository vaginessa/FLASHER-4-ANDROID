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
}