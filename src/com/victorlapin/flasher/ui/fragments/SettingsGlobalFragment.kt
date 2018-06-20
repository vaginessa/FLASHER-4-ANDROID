package com.victorlapin.flasher.ui.fragments

import android.os.Bundle
import android.os.Environment
import android.support.v7.preference.ListPreference
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceFragmentCompat
import com.victorlapin.flasher.R
import com.victorlapin.flasher.manager.ResourcesManager
import com.victorlapin.flasher.manager.SettingsManager
import com.victorlapin.flasher.model.interactor.RecoveryScriptInteractor
import com.victorlapin.flasher.ui.activities.BaseActivity
import com.victorlapin.flasher.ui.activities.SettingsActivity
import org.koin.android.ext.android.inject

class SettingsGlobalFragment : PreferenceFragmentCompat() {
    private val mSettings by inject<SettingsManager>()
    private val mResources by inject<ResourcesManager>()
    private val mScriptInteractor by inject<RecoveryScriptInteractor>()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences_global)

        val themePreference = findPreference(SettingsManager.KEY_THEME) as ListPreference
        val entries = arrayOf(
                getString(R.string.theme_light),
                getString(R.string.theme_dark),
                getString(R.string.theme_black)
        )
        val values = arrayOf(
                Integer.toString(R.style.AppTheme_Light),
                Integer.toString(R.style.AppTheme_Dark),
                Integer.toString(R.style.AppTheme_Black)
        )

        val value = mSettings.themeString

        themePreference.entries = entries
        themePreference.entryValues = values
        themePreference.value = value
        themePreference.onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { _, newValue ->
                    (activity as BaseActivity)
                            .updateTheme(Integer.valueOf(newValue as String))
                    true
                }

        findPreference(SettingsManager.KEY_ABOUT).onPreferenceClickListener =
                Preference.OnPreferenceClickListener {
                    (activity as SettingsActivity).presenter.openAbout()
                    return@OnPreferenceClickListener true
                }

        findPreference(SettingsManager.KEY_SAVE_DEBUG_SCRIPT).summary =
                mResources.getString(R.string.pref_summary_save_debug_script)
                        .format(Environment.getExternalStorageDirectory().absolutePath)

        findPreference(SettingsManager.KEY_DELETE_DEPLOYED_SCRIPT).onPreferenceClickListener =
                Preference.OnPreferenceClickListener {
                    mScriptInteractor.deleteScript()
                    return@OnPreferenceClickListener true
                }
    }

    companion object {
        fun newInstance() = SettingsGlobalFragment()
    }
}