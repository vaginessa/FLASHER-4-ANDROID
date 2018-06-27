package com.victorlapin.flasher.ui.activities

import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.Theme
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.victorlapin.flasher.R
import com.victorlapin.flasher.Screens
import com.victorlapin.flasher.manager.SettingsManager
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

    private val mSettings by inject<SettingsManager>()

    override fun onStop() {
        super.onStop()
        release(Screens.ACTIVITY_REBOOT_DIALOG)
    }

    override fun showRebootDialog() {
        val theme = when (mSettings.theme) {
            R.style.AppTheme_Light -> Theme.LIGHT
            else -> Theme.DARK
        }

        MaterialDialog.Builder(this)
                .theme(theme)
                .title(R.string.app_name)
                .content(R.string.reboot)
                .positiveText(android.R.string.yes)
                .positiveColorAttr(R.attr.colorAccent)
                .onPositive { _, _ -> presenter.rebootRecovery() }
                .negativeText(android.R.string.no)
                .negativeColorAttr(R.attr.colorAccent)
                .cancelListener { finish() }
                .show()
    }
}