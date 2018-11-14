package com.victorlapin.flasher.ui.activities

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.victorlapin.flasher.Const
import com.victorlapin.flasher.R
import com.victorlapin.flasher.manager.ServicesManager
import com.victorlapin.flasher.presenter.MainActivityPresenter
import com.victorlapin.flasher.ui.Biometric
import com.victorlapin.flasher.ui.fragments.AboutFragment
import com.victorlapin.flasher.ui.fragments.SettingsGlobalFragment
import com.victorlapin.flasher.view.MainActivityView
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import ru.terrakok.cicerone.androidx.SupportAppNavigator
import ru.terrakok.cicerone.commands.Command

class MainActivity : BaseActivity(), MainActivityView {
    override val layoutRes = R.layout.activity_generic_no_toolbar
    override val scopeName = Const.ACTIVITY_MAIN

    @InjectPresenter
    lateinit var presenter: MainActivityPresenter

    @ProvidePresenter
    fun providePresenter() = get<MainActivityPresenter>()

    private val mServices by inject<ServicesManager>()

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
            Biometric.askFingerprint(
                    activity = this,
                    title = R.string.fingerprint_auth_title,
                    successListener = { mShouldAuth = false },
                    cancelListener = { presenter.exit() }
            )
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