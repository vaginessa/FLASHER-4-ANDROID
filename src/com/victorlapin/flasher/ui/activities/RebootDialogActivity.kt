package com.victorlapin.flasher.ui.activities

import com.afollestad.materialdialogs.MaterialDialog
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.victorlapin.flasher.R
import com.victorlapin.flasher.Screens
import com.victorlapin.flasher.presenter.RebootDialogActivityPresenter
import com.victorlapin.flasher.view.RebootDialogActivityView
import org.koin.android.ext.android.inject
import org.koin.android.ext.android.release

class RebootDialogActivity : MvpAppCompatActivity(), RebootDialogActivityView {
    private val mPresenter by inject<RebootDialogActivityPresenter>()

    @InjectPresenter
    lateinit var presenter: RebootDialogActivityPresenter

    @ProvidePresenter
    fun providePresenter() = mPresenter

    override fun onStop() {
        super.onStop()
        release(Screens.ACTIVITY_REBOOT_DIALOG)
    }

    override fun showRebootDialog() {
        MaterialDialog(this)
                .title(res = R.string.app_name)
                .message(res = R.string.reboot)
                .positiveButton(res = android.R.string.yes) {
                    presenter.rebootRecovery()
                }
                .negativeButton(res = android.R.string.no) {
                    finish()
                }
                .show()
    }
}