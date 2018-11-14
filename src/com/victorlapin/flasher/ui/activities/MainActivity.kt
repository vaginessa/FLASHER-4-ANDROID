package com.victorlapin.flasher.ui.activities

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.biometrics.BiometricConstants
import androidx.biometrics.BiometricPrompt
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.victorlapin.flasher.Const
import com.victorlapin.flasher.R
import com.victorlapin.flasher.manager.ResourcesManager
import com.victorlapin.flasher.manager.ServicesManager
import com.victorlapin.flasher.presenter.MainActivityPresenter
import com.victorlapin.flasher.ui.HandlerExecutor
import com.victorlapin.flasher.ui.fragments.AboutFragment
import com.victorlapin.flasher.ui.fragments.SettingsGlobalFragment
import com.victorlapin.flasher.view.MainActivityView
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import ru.terrakok.cicerone.androidx.SupportAppNavigator
import ru.terrakok.cicerone.commands.Command
import timber.log.Timber

class MainActivity : BaseActivity(), MainActivityView {
    override val layoutRes = R.layout.activity_generic_no_toolbar
    override val scopeName = Const.ACTIVITY_MAIN

    @InjectPresenter
    lateinit var presenter: MainActivityPresenter

    @ProvidePresenter
    fun providePresenter() = get<MainActivityPresenter>()

    private val mServices by inject<ServicesManager>()
    private val mResources by inject<ResourcesManager>()

    private var mShouldAuth = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        savedInstanceState?.let {
            mShouldAuth = it.getBoolean(ARG_SHOULD_AUTH)
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putBoolean(ARG_SHOULD_AUTH, mShouldAuth)
    }

    override fun askFingerprint() {
        if (mShouldAuth && mServices.isFingerprintAvailable()) {
            val builder = BiometricPrompt.PromptInfo.Builder().apply {
                setTitle(mResources.getString(R.string.fingerprint_auth_title))
                setNegativeButtonText(mResources.getString(android.R.string.cancel))
            }
            val callback = object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    Timber.i("Fingerprint check success")
                    mShouldAuth = false
                }

                @SuppressLint("SwitchIntDef")
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    Timber.i("Fingerprint check error: $errString")
                    when (errorCode) {
                        BiometricConstants.ERROR_NEGATIVE_BUTTON,
                        BiometricConstants.ERROR_USER_CANCELED -> presenter.exit()
                        else -> super.onAuthenticationError(errorCode, errString)
                    }
                }
            }

            BiometricPrompt(this, HandlerExecutor(), callback)
                    .authenticate(builder.build())
        }
    }

    override val navigator = object : SupportAppNavigator(this, R.id.fragment_container) {
        override fun setupFragmentTransaction(command: Command?,
                                              currentFragment: Fragment?,
                                              nextFragment: Fragment?,
                                              fragmentTransaction: FragmentTransaction?) {
            nextFragment?.let { _ ->
                fragmentTransaction?.let {
                    if (nextFragment is SettingsGlobalFragment || nextFragment is AboutFragment) {
                        it.setCustomAnimations(R.animator.slide_up, R.animator.fade_out,
                                R.animator.fade_in, R.animator.slide_down)
                    }
                }
            }
        }
    }

    companion object {
        private const val ARG_SHOULD_AUTH = "ARG_SHOULD_AUTH"
    }
}