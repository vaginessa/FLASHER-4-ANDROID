package com.victorlapin.flasher.ui.fragments

import android.Manifest
import android.os.Bundle
import androidx.preference.*
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.files.folderChooser
import com.tbruyelle.rxpermissions2.RxPermissions
import com.victorlapin.flasher.Const
import com.victorlapin.flasher.R
import com.victorlapin.flasher.manager.LogManager
import com.victorlapin.flasher.manager.ResourcesManager
import com.victorlapin.flasher.manager.ServicesManager
import com.victorlapin.flasher.manager.SettingsManager
import com.victorlapin.flasher.model.interactor.AlarmInteractor
import com.victorlapin.flasher.model.interactor.RecoveryScriptInteractor
import com.victorlapin.flasher.ui.Biometric
import com.victorlapin.flasher.ui.activities.BaseActivity
import com.victorlapin.flasher.ui.activities.MainActivity
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.android.inject
import org.koin.core.scope.Scope
import java.io.File

class SettingsGlobalFragment : PreferenceFragmentCompat() {
    private lateinit var mScope: Scope
    private val mSettings by inject<SettingsManager>()
    private val mResources by inject<ResourcesManager>()
    private val mServices by inject<ServicesManager>()
    private val mLogs by inject<LogManager>()
    private val mScriptInteractor by inject<RecoveryScriptInteractor>()
    private val mAlarmInteractor by inject<AlarmInteractor>()

    private val mRxPermissions by lazy {
        RxPermissions(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        mScope = getKoin().createScope(Const.FRAGMENT_SETTINGS)
        super.onCreate(savedInstanceState)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences_global)

        val themePreference = findPreference(SettingsManager.KEY_THEME) as ListPreference
        val entries = arrayOf(
                mResources.getString(R.string.theme_light),
                mResources.getString(R.string.theme_light_pixel),
                mResources.getString(R.string.theme_dark),
                mResources.getString(R.string.theme_dark_pixel),
                mResources.getString(R.string.theme_black),
                mResources.getString(R.string.theme_black_pixel)
        )
        val values = arrayOf(
                Integer.toString(R.style.AppTheme_Light),
                Integer.toString(R.style.AppTheme_Light_Pixel),
                Integer.toString(R.style.AppTheme_Dark),
                Integer.toString(R.style.AppTheme_Dark_Pixel),
                Integer.toString(R.style.AppTheme_Black),
                Integer.toString(R.style.AppTheme_Black_Pixel)
        )

        val value = mSettings.themeString

        themePreference.entries = entries
        themePreference.entryValues = values
        themePreference.value = value
        themePreference.setOnPreferenceChangeListener { _, newValue ->
            (activity as BaseActivity)
                    .updateTheme(Integer.valueOf(newValue as String))
            true
        }

        findPreference(SettingsManager.KEY_ABOUT).setOnPreferenceClickListener {
            (activity as MainActivity).presenter.selectAbout()
            true
        }

        findPreference(SettingsManager.KEY_DELETE_DEPLOYED_SCRIPT).setOnPreferenceClickListener {
            mScriptInteractor.deleteScript().subscribe()
            true
        }

        findPreference(SettingsManager.KEY_CLEAR_SCHEDULE).setOnPreferenceClickListener {
            mSettings.apply {
                mAlarmInteractor.cancelAlarm()
                        .doOnComplete {
                            useSchedule = false
                            scheduleTime = 0
                            scheduleInterval = 0
                            scheduleLastRun = 0
                            scheduleOnlyCharging = false
                            scheduleOnlyIdle = false
                            scheduleOnlyHighBattery = false
                        }
                        .subscribe()
            }
            true
        }

        val backupsToKeepPreference = findPreference(SettingsManager.KEY_BACKUPS_TO_KEEP)
                as EditTextPreference
        backupsToKeepPreference.summary = mSettings.backupsToKeep.toString()
        backupsToKeepPreference.setOnPreferenceChangeListener { it, newValue ->
            try {
                val i = newValue.toString().trim().toInt()
                it.summary = i.toString()
                true
            } catch (ignore: Exception) {
                false
            }
        }

        val backupsPathPreference = findPreference(SettingsManager.KEY_BACKUPS_PATH)
        backupsPathPreference.summary = mSettings.backupsPath
        backupsPathPreference.setOnPreferenceClickListener {
            mRxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .firstOrError()
                    .subscribe { granted ->
                        if (granted) {
                            MaterialDialog(context!!)
                                    .folderChooser(initialDirectory = File(mSettings.backupsPath),
                                            emptyTextRes = R.string.folder_empty) { _, folder ->
                                        mSettings.backupsPath = folder.absolutePath
                                        it.summary = folder.absolutePath
                                    }
                                    .positiveButton(res = android.R.string.ok)
                                    .negativeButton(res = android.R.string.cancel)
                                    .show()
                        }
                    }
            true
        }

        val logPreference = findPreference(SettingsManager.KEY_ENABLE_FILE_LOG)
        logPreference.summary = mResources.getString(R.string.pref_summary_enable_file_log)
                .format(Const.LOG_FILENAME)
        logPreference.setOnPreferenceChangeListener { it, newValue ->
            val b = newValue as Boolean
            if (b) {
                mRxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .firstOrError()
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
            true
        }

        val isFPAvailable = mServices.isFingerprintAvailable()
        val fpPreference = findPreference(SettingsManager.KEY_ASK_FINGERPRINT_ON_LAUNCH)
                as SwitchPreference
        val fpRebootPreference = findPreference(SettingsManager.KEY_ASK_FINGERPRINT_TO_REBOOT)
                as SwitchPreference
        if (isFPAvailable) {
            fpPreference.isEnabled = true
            fpRebootPreference.isEnabled = true
            fpPreference.setOnPreferenceChangeListener { it, newValue ->
                val b = newValue as Boolean
                if (b) {
                    Biometric.askFingerprint(
                            activity = activity!!,
                            title = R.string.fingerprint_setting_title,
                            description = R.string.fingerprint_setting_description,
                            cancelListener = {
                                mSettings.askFingerprintOnLaunch = false
                                (it as SwitchPreference).isChecked = false
                            }
                    )
                }
                true
            }

            fpRebootPreference.setOnPreferenceChangeListener { it, newValue ->
                val b = newValue as Boolean
                if (b) {
                    Biometric.askFingerprint(
                            activity = activity!!,
                            title = R.string.fingerprint_setting_title,
                            description = R.string.fingerprint_setting_description,
                            cancelListener = {
                                mSettings.askFingerprintToReboot = false
                                (it as SwitchPreference).isChecked = false
                            }
                    )
                }
                true
            }
        } else {
            mSettings.askFingerprintOnLaunch = false
            fpPreference.isChecked = false
            fpPreference.isEnabled = false
            mSettings.askFingerprintToReboot = false
            fpRebootPreference.isChecked = false
            fpRebootPreference.isEnabled = false
        }
    }

    override fun onStop() {
        super.onStop()
        mScope.close()
    }

    companion object {
        fun newInstance() = SettingsGlobalFragment()
    }
}