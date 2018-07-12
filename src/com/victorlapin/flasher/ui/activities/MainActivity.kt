package com.victorlapin.flasher.ui.activities

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v4.app.Fragment
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
import org.koin.android.ext.android.inject
import org.koin.android.ext.android.release
import ru.terrakok.cicerone.android.SupportAppNavigator

class MainActivity : BaseActivity(), MainActivityView {
    override val layoutRes = R.layout.activity_generic_no_toolbar

    private val mPresenter by inject<MainActivityPresenter>()

    @InjectPresenter
    lateinit var presenter: MainActivityPresenter

    @ProvidePresenter
    fun providePresenter() = mPresenter

    override fun onStop() {
        super.onStop()
        release(Screens.ACTIVITY_MAIN)
    }

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
    }
}