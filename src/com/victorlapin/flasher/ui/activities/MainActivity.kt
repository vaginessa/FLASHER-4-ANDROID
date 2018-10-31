package com.victorlapin.flasher.ui.activities

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.mtramin.rxfingerprint.RxFingerprint
import com.victorlapin.flasher.Const
import com.victorlapin.flasher.R
import com.victorlapin.flasher.presenter.MainActivityPresenter
import com.victorlapin.flasher.ui.fragments.AboutFragment
import com.victorlapin.flasher.ui.fragments.FingerprintAuthFragment
import com.victorlapin.flasher.ui.fragments.SettingsGlobalFragment
import com.victorlapin.flasher.view.MainActivityView
import org.koin.android.ext.android.get
import ru.terrakok.cicerone.android.support.SupportAppNavigator
import ru.terrakok.cicerone.commands.Command

class MainActivity : BaseActivity(), MainActivityView {
    override val layoutRes = R.layout.activity_generic_no_toolbar
    override val scopeName = Const.ACTIVITY_MAIN

    @InjectPresenter
    lateinit var presenter: MainActivityPresenter

    @ProvidePresenter
    fun providePresenter() = get<MainActivityPresenter>()

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
        if (mShouldAuth && RxFingerprint.isAvailable(this)) {
            val oldFragment = supportFragmentManager
                    .findFragmentByTag(FingerprintAuthFragment::class.java.simpleName)
            if (oldFragment != null) {
                (oldFragment as FingerprintAuthFragment).successListener = { mShouldAuth = false }
                oldFragment.cancelListener = { presenter.exit() }
            } else {
                val fragment = FingerprintAuthFragment.newInstance(
                        successListener = { mShouldAuth = false },
                        cancelListener = { presenter.exit() }
                )
                fragment.show(supportFragmentManager,
                        FingerprintAuthFragment::class.java.simpleName)
            }
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