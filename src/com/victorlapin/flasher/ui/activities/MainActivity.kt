package com.victorlapin.flasher.ui.activities

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.victorlapin.flasher.R
import com.victorlapin.flasher.Screens
import com.victorlapin.flasher.presenter.MainActivityPresenter
import com.victorlapin.flasher.ui.fragments.AboutFragment
import com.victorlapin.flasher.ui.fragments.HomeFragment
import com.victorlapin.flasher.ui.fragments.ScheduleFragment
import com.victorlapin.flasher.ui.fragments.SettingsGlobalFragment
import com.victorlapin.flasher.view.MainActivityView
import org.koin.android.ext.android.get
import ru.terrakok.cicerone.android.SupportAppNavigator
import ru.terrakok.cicerone.commands.Command

class MainActivity : BaseActivity(), MainActivityView {
    override val layoutRes = R.layout.activity_generic_no_toolbar
    override val scopeName = Screens.ACTIVITY_MAIN

    @InjectPresenter
    lateinit var presenter: MainActivityPresenter

    @ProvidePresenter
    fun providePresenter() = get<MainActivityPresenter>()

    override val navigator = object : SupportAppNavigator(this, R.id.fragment_container) {
        override fun createActivityIntent(context: Context?, screenKey: String?, data: Any?): Intent? =
                when (screenKey) {
                    Screens.EXTERNAL_ABOUT -> {
                        data?.let {
                            return Intent(Intent.ACTION_VIEW, Uri.parse(data.toString()))
                        }
                        null
                    }
                    else -> null
                }

        override fun createFragment(screenKey: String?, data: Any?): Fragment? = when (screenKey) {
            Screens.FRAGMENT_HOME -> HomeFragment.newInstance()
            Screens.FRAGMENT_SCHEDULE -> ScheduleFragment.newInstance()
            Screens.FRAGMENT_SETTINGS -> SettingsGlobalFragment.newInstance()
            Screens.FRAGMENT_ABOUT -> AboutFragment.newInstance()
            else -> null
        }

        override fun setupFragmentTransactionAnimation(command: Command?,
                                                       currentFragment: Fragment?,
                                                       nextFragment: Fragment?,
                                                       fragmentTransaction: FragmentTransaction?) {
            nextFragment?.let {
                fragmentTransaction?.let {
                    if (nextFragment is SettingsGlobalFragment || nextFragment is AboutFragment) {
                        it.setCustomAnimations(R.animator.slide_up, R.animator.fade_out,
                                R.animator.fade_in, R.animator.slide_down)
                    }
                }
            }
        }
    }
}