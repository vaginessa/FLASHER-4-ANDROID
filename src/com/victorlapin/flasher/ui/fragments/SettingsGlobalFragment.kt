package com.victorlapin.flasher.ui.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.documentfile.provider.DocumentFile
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.afollestad.assent.Permission
import com.afollestad.assent.askForPermissions
import com.afollestad.assent.runWithPermissions
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.afollestad.materialdialogs.list.listItemsSingleChoice
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
import org.koin.android.ext.android.inject

class SettingsGlobalFragment : PreferenceFragmentCompat() {
    private val mSettings by inject<SettingsManager>()
    private val mResources by inject<ResourcesManager>()
    private val mServices by inject<ServicesManager>()
    private val mLogs by inject<LogManager>()
    private val mScriptInteractor by inject<RecoveryScriptInteractor>()
    private val mAlarmInteractor by inject<AlarmInteractor>()

    private lateinit var mBackupsPathPreference: Preference

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences_global)

        val themePreference = findPreference<Preference>(SettingsManager.KEY_THEME)
        val entries = listOf(
            mResources.getString(R.string.theme_light),
            mResources.getString(R.string.theme_light_pixel),
            mResources.getString(R.string.theme_dark),
            mResources.getString(R.string.theme_dark_pixel),
            mResources.getString(R.string.theme_black),
            mResources.getString(R.string.theme_black_pixel)
        )
        val values = listOf(
            Integer.toString(R.style.AppTheme_Light),
            Integer.toString(R.style.AppTheme_Light_Pixel),
            Integer.toString(R.style.AppTheme_Dark),
            Integer.toString(R.style.AppTheme_Dark_Pixel),
            Integer.toString(R.style.AppTheme_Black),
            Integer.toString(R.style.AppTheme_Black_Pixel)
        )
        val initialSelection = values.indexOf(mSettings.theme.toString())

        themePreference!!.setOnPreferenceClickListener {
            MaterialDialog(context!!).show {
                lifecycleOwner(this@SettingsGlobalFragment)
                title(R.string.pref_title_theme)
                listItemsSingleChoice(
                    items = entries,
                    initialSelection = initialSelection,
                    waitForPositiveButton = false
                ) { dialog, index, _ ->
                    dialog.dismiss()
                    val theme = Integer.valueOf(values[index])
                    mSettings.themeString = values[index]
                    (activity as BaseActivity).updateTheme(theme)
                }
                negativeButton(android.R.string.cancel)
            }
            true
        }

        findPreference<Preference>(SettingsManager.KEY_ABOUT)!!.setOnPreferenceClickListener {
            (activity as MainActivity).presenter.selectAbout()
            true
        }

        findPreference<Preference>(SettingsManager.KEY_DELETE_DEPLOYED_SCRIPT)!!.setOnPreferenceClickListener {
            mScriptInteractor.deleteScript().subscribe()
            true
        }

        findPreference<Preference>(SettingsManager.KEY_CLEAR_SCHEDULE)!!.setOnPreferenceClickListener {
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

        val backupsToKeepPreference =
            findPreference<Preference>(SettingsManager.KEY_BACKUPS_TO_KEEP)
        backupsToKeepPreference!!.summary = mSettings.backupsToKeep.toString()
        backupsToKeepPreference.setOnPreferenceClickListener { pref ->
            MaterialDialog(context!!).show {
                lifecycleOwner(this@SettingsGlobalFragment)
                title(R.string.pref_title_backups_to_keep)
                input(prefill = mSettings.backupsToKeep.toString()) { _, text ->
                    try {
                        val i = text.toString().trim().toInt()
                        if (i > 0) {
                            mSettings.backupsToKeep = i
                            pref.summary = i.toString()
                        }
                    } catch (ignore: Exception) {
                    }
                }
                positiveButton(android.R.string.ok)
                negativeButton(android.R.string.cancel)
            }
            true
        }

        mBackupsPathPreference = findPreference(SettingsManager.KEY_BACKUPS_PATH)!!
        mBackupsPathPreference.summary =
            if (mSettings.backupsPath != null) mSettings.backupsPath else {
                val spannable =
                    SpannableString(mResources.getString(R.string.pref_backups_path_empty))
                spannable.setSpan(
                    ForegroundColorSpan(Color.RED), 0, spannable.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                spannable
            }
        mBackupsPathPreference.setOnPreferenceClickListener {
            runWithPermissions(Permission.WRITE_EXTERNAL_STORAGE) {
                startActivityForResult(
                    Intent(Intent.ACTION_OPEN_DOCUMENT_TREE),
                    REQUEST_DOCUMENT_TREE
                )
            }
            true
        }

        val logPreference = findPreference<SwitchPreference>(SettingsManager.KEY_ENABLE_FILE_LOG)
        logPreference!!.summary = mResources.getString(R.string.pref_summary_enable_file_log)
            .format(Const.LOG_FILENAME)
        logPreference.setOnPreferenceChangeListener { it, newValue ->
            val b = newValue as Boolean
            if (b) {
                askForPermissions(Permission.WRITE_EXTERNAL_STORAGE) { result ->
                    if (result.isAllGranted(Permission.WRITE_EXTERNAL_STORAGE)) {
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
        val fpPreference =
            findPreference<SwitchPreference>(SettingsManager.KEY_ASK_FINGERPRINT_ON_LAUNCH)
        val fpRebootPreference =
            findPreference<SwitchPreference>(SettingsManager.KEY_ASK_FINGERPRINT_TO_REBOOT)
        if (isFPAvailable) {
            fpPreference!!.isEnabled = true
            fpRebootPreference!!.isEnabled = true
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
            fpPreference!!.isChecked = false
            fpPreference.isEnabled = false
            mSettings.askFingerprintToReboot = false
            fpRebootPreference!!.isChecked = false
            fpRebootPreference.isEnabled = false
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (requestCode == REQUEST_DOCUMENT_TREE && resultCode == Activity.RESULT_OK) {
            intent?.let { data ->
                val flags = Intent.FLAG_GRANT_WRITE_URI_PERMISSION or
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                val uri = data.data!!
                val uriString = uri.toString()
                mSettings.backupsPath?.let {
                    if (it != uriString) {
                        val oldDir = DocumentFile.fromSingleUri(context!!, Uri.parse(it))
                        oldDir?.let { dir ->
                            try {
                                context!!.contentResolver.releasePersistableUriPermission(
                                    dir.uri,
                                    flags
                                )
                            } catch (ignore: SecurityException) {
                            }
                        }
                    }
                }
                context!!.contentResolver.takePersistableUriPermission(uri, flags)
                mSettings.backupsPath = uri.toString()
                mBackupsPathPreference.summary = uri.toString()
            }
        }
    }

    companion object {
        private const val REQUEST_DOCUMENT_TREE = 112

        fun newInstance() = SettingsGlobalFragment()
    }
}