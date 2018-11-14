package com.victorlapin.flasher.ui.activities

import android.annotation.SuppressLint
import androidx.biometrics.BiometricConstants
import androidx.biometrics.BiometricPrompt
import com.afollestad.materialdialogs.MaterialDialog
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.victorlapin.flasher.Const
import com.victorlapin.flasher.R
import com.victorlapin.flasher.manager.ResourcesManager
import com.victorlapin.flasher.manager.ServicesManager
import com.victorlapin.flasher.presenter.RebootDialogActivityPresenter
import com.victorlapin.flasher.ui.HandlerExecutor
import com.victorlapin.flasher.view.RebootDialogActivityView
import org.koin.android.ext.android.get
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.android.inject
import timber.log.Timber

class RebootDialogActivity : MvpAppCompatActivity(), RebootDialogActivityView {
    private val mScope = getKoin().createScope(Const.ACTIVITY_REBOOT_DIALOG)

    @InjectPresenter
    lateinit var presenter: RebootDialogActivityPresenter

    @ProvidePresenter
    fun providePresenter() = get<RebootDialogActivityPresenter>()

    private val mServices by inject<ServicesManager>()
    private val mResources by inject<ResourcesManager>()

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
            val builder = BiometricPrompt.PromptInfo.Builder().apply {
                setTitle(mResources.getString(R.string.fingerprint_auth_title))
                setNegativeButtonText(mResources.getString(android.R.string.cancel))
            }
            val callback = object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    Timber.i("Fingerprint check success")
                    presenter.rebootRecovery()
                    finish()
                }

                @SuppressLint("SwitchIntDef")
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    Timber.i("Fingerprint check error: $errString")
                    when (errorCode) {
                        BiometricConstants.ERROR_NEGATIVE_BUTTON,
                        BiometricConstants.ERROR_USER_CANCELED -> finish()
                        else -> super.onAuthenticationError(errorCode, errString)
                    }
                }
            }

            BiometricPrompt(this, HandlerExecutor(), callback)
                    .authenticate(builder.build())
        } else {
            presenter.rebootRecovery()
            finish()
        }
    }
}