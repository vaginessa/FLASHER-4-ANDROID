package com.victorlapin.flasher.ui.activities

import com.afollestad.materialdialogs.MaterialDialog
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.mtramin.rxfingerprint.RxFingerprint
import com.victorlapin.flasher.Const
import com.victorlapin.flasher.R
import com.victorlapin.flasher.presenter.RebootDialogActivityPresenter
import com.victorlapin.flasher.ui.fragments.FingerprintRebootFragment
import com.victorlapin.flasher.view.RebootDialogActivityView
import org.koin.android.ext.android.get
import org.koin.android.ext.android.getKoin

class RebootDialogActivity : MvpAppCompatActivity(), RebootDialogActivityView {
    private val mScope = getKoin().createScope(Const.ACTIVITY_REBOOT_DIALOG)

    @InjectPresenter
    lateinit var presenter: RebootDialogActivityPresenter

    @ProvidePresenter
    fun providePresenter() = get<RebootDialogActivityPresenter>()

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
        if (RxFingerprint.isAvailable(this)) {
            val oldFragment = supportFragmentManager
                    .findFragmentByTag(FingerprintRebootFragment::class.java.simpleName)
            if (oldFragment != null) {
                (oldFragment as FingerprintRebootFragment)
                        .successListener = { presenter.rebootRecovery() }
            } else {
                val fragment = FingerprintRebootFragment.newInstance(
                        successListener = { presenter.rebootRecovery() }
                )
                fragment.show(supportFragmentManager,
                        FingerprintRebootFragment::class.java.simpleName)
            }
        } else {
            presenter.rebootRecovery()
        }
    }
}