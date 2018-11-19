package com.victorlapin.flasher.ui.activities

import com.afollestad.materialdialogs.MaterialDialog
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.victorlapin.flasher.Const
import com.victorlapin.flasher.R
import com.victorlapin.flasher.manager.ServicesManager
import com.victorlapin.flasher.presenter.RebootDialogActivityPresenter
import com.victorlapin.flasher.ui.Biometric
import com.victorlapin.flasher.view.RebootDialogActivityView
import org.koin.android.ext.android.get
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.android.inject

class RebootDialogActivity : MvpAppCompatActivity(), RebootDialogActivityView {
    private val mScope = getKoin().createScope(Const.ACTIVITY_REBOOT_DIALOG)

    @InjectPresenter
    lateinit var presenter: RebootDialogActivityPresenter

    @ProvidePresenter
    fun providePresenter() = get<RebootDialogActivityPresenter>()

    private val mServices by inject<ServicesManager>()

    override fun onStop() {
        super.onStop()
        mScope.close()
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