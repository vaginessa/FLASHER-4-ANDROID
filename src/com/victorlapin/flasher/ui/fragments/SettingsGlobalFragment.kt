package com.victorlapin.flasher.ui.fragments

import android.Manifest
import android.os.Bundle
import android.support.v14.preference.SwitchPreference
import android.support.v7.preference.EditTextPreference
import android.support.v7.preference.ListPreference
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceFragmentCompat
import com.tbruyelle.rxpermissions2.RxPermissions
import com.victorlapin.flasher.Const
import com.victorlapin.flasher.R
import com.victorlapin.flasher.manager.LogManager
import com.victorlapin.flasher.manager.ResourcesManager
import com.victorlapin.flasher.manager.SettingsManager
import com.victorlapin.flasher.model.interactor.AlarmInteractor
import com.victorlapin.flasher.model.interactor.RecoveryScriptInteractor
import com.victorlapin.flasher.ui.activities.BaseActivity
import com.victorlapin.flasher.ui.activities.MainActivity
import org.koin.android.ext.android.inject

class SettingsGlobalFragment : PreferenceFragmentCompat() {
    private val mSettings by inject<SettingsManager>()
    private val mResources by inject<ResourcesManager>()
    private val mLogs by inject<LogManager>()
    private val mScriptInteractor by inject<RecoveryScriptInteractor>()
    private val mAlarmInteractor by inject<AlarmInteractor>()

    private val mRxPermissions by lazy {
        RxPermissions(this)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences_global)

        val themePreference = findPreference(SettingsManager.KEY_THEME) as ListPreference
        val entries = arrayOf(
                mResources.getString(R.string.theme_light),
                mResources.getString(R.string.theme_dark),
                mResources.getString(R.string.theme_black)
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
                    (activity as MainActivity).presenter.selectAbout()
                    return@OnPreferenceClickListener true
                }

        findPreference(SettingsManager.KEY_DELETE_DEPLOYED_SCRIPT).onPreferenceClickListener =
                Preference.OnPreferenceClickListener {
                    mScriptInteractor.deleteScript()
                    return@OnPreferenceClickListener true
                }

        findPreference(SettingsManager.KEY_CLEAR_SCHEDULE).onPreferenceClickListener =
                Preference.OnPreferenceClickListener {
                    mSettings.apply {
                        mAlarmInteractor.cancelAlarm()
                                .doOnSuccess {
                                    useSchedule = false
                                    scheduleTime = 0
                                    scheduleInterval = 0
                                    scheduleLastRun = 0
                                }
                                .subscribe()
                    }
                    return@OnPreferenceClickListener true
                }

        val backupsToKeepPreference = findPreference(SettingsManager.KEY_BACKUPS_TO_KEEP)
                as EditTextPreference
        backupsToKeepPreference.summary = mSettings.backupsToKeep.toString()
        backupsToKeepPreference.onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { it, newValue ->
                    try {
                        val i = newValue.toString().trim().toInt()
                        it.summary = i.toString()
                        return@OnPreferenceChangeListener true
                    } catch (ignore: Exception) {
                        return@OnPreferenceChangeListener false
                    }
                }

        val logPreference = findPreference(SettingsManager.KEY_ENABLE_FILE_LOG)
        logPreference.summary = mResources.getString(R.string.pref_summary_enable_file_log)
                .format(Const.LOG_FILENAME)
        logPreference.onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { it, newValue ->
                    val b = newValue as Boolean
                    if (b) {
                        mRxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                .subscribe { granted ->
                                    if (granted) {
                                        mLogs.enableFileLogs()
                                    } else {
                                        mLogs.disableFileLogs()
                                        mSettings.enableFileLog = false
                                        (it as SwitchPreference).isChecked = false
                                    }
                                }
                    } else {
                        mLogs.disableFileLogs()
                    }
                    return@OnPreferenceChangeListener true
                }
    }

    companion object {
        fun newInstance() = SettingsGlobalFragment()
    }
}