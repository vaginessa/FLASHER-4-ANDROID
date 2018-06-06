package com.victorlapin.flasher.manager

import android.content.Context
import android.support.v7.preference.PreferenceManager
import com.victorlapin.flasher.R

class SettingsManager(context: Context) {
    companion object {
        const val KEY_THEME = "interface_theme"
        const val KEY_LAST_USED_PATH = "last_used_path"
    }

    private val mPrefs = PreferenceManager.getDefaultSharedPreferences(context)

    val themeString: String
        get() = mPrefs.getString(KEY_THEME, R.style.AppTheme_Light.toString())

    val theme: Int
        get() = Integer.parseInt(themeString)

    var lastUsedPath: String?
        get() = mPrefs.getString(KEY_LAST_USED_PATH, null)
        set(path) = mPrefs.edit().putString(KEY_LAST_USED_PATH, path).apply()
}