package com.victorlapin.flasher.ui.activities

import android.os.Bundle
import com.afollestad.materialdialogs.MaterialDialog
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.victorlapin.flasher.R
import com.victorlapin.flasher.manager.ServicesManager
import com.victorlapin.flasher.manager.SettingsManager
import com.victorlapin.flasher.presenter.RebootDialogActivityPresenter
import com.victorlapin.flasher.ui.Biometric
import com.victorlapin.flasher.view.RebootDialogActivityView
import org.koin.android.ext.android.inject
import org.koin.androidx.scope.bindScope
import org.koin.androidx.scope.getActivityScope

class RebootDialogActivity : MvpAppCompatActivity(), RebootDialogActivityView {
    @InjectPresenter
    lateinit var presenter: RebootDialogActivityPresenter

    @ProvidePresenter
    fun providePresenter() = getActivityScope().get<RebootDialogActivityPresenter>()

    private val mServices by inject<ServicesManager>()
    private val mSettings by inject<SettingsManager>()

    override fun onCreate(savedInstanceState: Bundle?) {
        bindScope(getActivityScope())
        val theme = when (mSettings.theme) {
            R.style.AppTheme_Light -> R.style.AppTheme_Transparent_Light
            R.style.AppTheme_Light_Pixel -> R.style.AppTheme_Transparent_Light_Pixel
            R.style.AppTheme_Dark,
            R.style.AppTheme_Black -> R.style.AppTheme_Transparent_Dark
            else -> R.style.AppTheme_Transparent_Dark_Pixel
        }
        setTheme(theme)
        super.onCreate(savedInstanceState)
    }

    override fun showRebootDialog() {
        MaterialDialog(this)
            .title(res = R.string.app_name)
            .message(res = R.string.reboot)
            .positiveButton(res = android.R.string.yes) {
                presenter.onRebootRequested()
            }
            .negativeButton(res = android.R.string.no) {
                finish()
            }
            .show()
    }

    override fun askFingerprint() {
        if (mServices.isFingerprintAvailable()) {
            Biometric.askFingerprint(
                activity = this,
                title = R.string.fingerprint_reboot_title,
                description = R.string.fingerprint_reboot_description,
                successListener = { presenter.rebootRecovery(); finish() },
                cancelListener = { finish() }
            )
        } else {
            presenter.rebootRecovery()
            finish()
        }
    }
}