package com.victorlapin.flasher.ui.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.Theme
import com.victorlapin.flasher.R
import com.victorlapin.flasher.manager.SettingsManager
import com.victorlapin.flasher.model.interactor.RecoveryScriptInteractor
import org.koin.android.ext.android.inject

class RebootDialogActivity : AppCompatActivity() {
    private val mScriptInteractor by inject<RecoveryScriptInteractor>()
    private val mSettings by inject<SettingsManager>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val theme = when (mSettings.theme) {
            R.style.AppTheme_Light -> Theme.LIGHT
            else -> Theme.DARK
        }

        MaterialDialog.Builder(this)
                .theme(theme)
                .title(R.string.app_name)
                .content(R.string.reboot)
                .positiveText(android.R.string.yes)
                .positiveColorRes(R.color.accent)
                .onPositive { _, _ -> mScriptInteractor.rebootRecovery() }
                .negativeText(android.R.string.no)
                .negativeColorRes(R.color.accent)
                .cancelListener { finish() }
                .show()
    }
}