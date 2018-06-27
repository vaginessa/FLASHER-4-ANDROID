package com.victorlapin.flasher.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.victorlapin.flasher.R
import com.victorlapin.flasher.Screens
import com.victorlapin.flasher.presenter.SettingsActivityPresenter
import com.victorlapin.flasher.ui.fragments.SettingsGlobalFragment
import com.victorlapin.flasher.view.SettingsActivityView
import org.koin.android.ext.android.inject
import org.koin.android.ext.android.release
import ru.terrakok.cicerone.android.SupportAppNavigator

class SettingsActivity : BaseActivity(), SettingsActivityView {
    override val layoutRes = R.layout.activity_generic

    private val mPresenter by inject<SettingsActivityPresenter>()

    @InjectPresenter
    lateinit var presenter: SettingsActivityPresenter

    @ProvidePresenter
    fun providePresenter() = mPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onStop() {
        super.onStop()
        release(Screens.ACTIVITY_SETTINGS)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() = presenter.onBackPressed()

    override val navigator = object : SupportAppNavigator(this, R.id.fragment_container) {
        override fun createActivityIntent(context: Context?, screenKey: String?, data: Any?): Intent? =
                when (screenKey) {
                    Screens.ACTIVITY_ABOUT -> Intent(context, AboutActivity::class.java)
                    else -> null
                }

        override fun createFragment(screenKey: String?, data: Any?): Fragment? = when (screenKey) {
            Screens.FRAGMENT_SETTINGS -> SettingsGlobalFragment.newInstance()
            else -> null
        }
    }
}