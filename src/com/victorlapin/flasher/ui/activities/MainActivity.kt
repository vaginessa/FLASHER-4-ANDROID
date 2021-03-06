package com.victorlapin.flasher.ui.activities

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.victorlapin.flasher.R
import com.victorlapin.flasher.presenter.MainActivityPresenter
import com.victorlapin.flasher.ui.Biometric
import com.victorlapin.flasher.ui.fragments.AboutFragment
import com.victorlapin.flasher.ui.fragments.SettingsGlobalFragment
import com.victorlapin.flasher.view.MainActivityView
import org.koin.androidx.scope.currentScope
import ru.terrakok.cicerone.android.support.SupportAppNavigator
import ru.terrakok.cicerone.commands.Command

class MainActivity : BaseActivity(), MainActivityView {
    override val layoutRes = R.layout.activity_generic_no_toolbar

    @InjectPresenter
    lateinit var presenter: MainActivityPresenter

    @ProvidePresenter
    fun providePresenter() = currentScope.get<MainActivityPresenter>()

    override fun askFingerprint() {
        Biometric.askFingerprint(
            activity = this,
            title = R.string.fingerprint_auth_title,
            description = R.string.fingerprint_auth_description,
            successListener = { presenter.onSuccessfulFingerprint() },
            cancelListener = { presenter.exit() }
        )
    }

    override fun cancelFingerprint() =
        Biometric.cancelFingerprint(supportFragmentManager)

    override val navigator = object : SupportAppNavigator(this, R.id.fragment_container) {
        override fun setupFragmentTransaction(
            command: Command?,
            currentFragment: Fragment?,
            nextFragment: Fragment?,
            fragmentTransaction: FragmentTransaction?
        ) {
            nextFragment?.let {
                fragmentTransaction?.let {
                    if (nextFragment is SettingsGlobalFragment || nextFragment is AboutFragment) {
                        it.setCustomAnimations(
                            R.animator.slide_up, R.animator.fade_out,
                            R.animator.fade_in, R.animator.slide_down
                        )
                    }
                }
            }
        }
    }
}