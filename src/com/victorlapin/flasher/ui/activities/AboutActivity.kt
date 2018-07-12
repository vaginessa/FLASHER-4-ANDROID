package com.victorlapin.flasher.ui.activities

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.victorlapin.flasher.R
import com.victorlapin.flasher.Screens
import com.victorlapin.flasher.presenter.AboutActivityPresenter
import com.victorlapin.flasher.ui.fragments.AboutFragment
import com.victorlapin.flasher.view.AboutActivityView
import kotlinx.android.synthetic.main.include_toolbar.*
import org.koin.android.ext.android.inject
import org.koin.android.ext.android.release
import ru.terrakok.cicerone.android.SupportAppNavigator

class AboutActivity: BaseActivity(), AboutActivityView {
    override val layoutRes = R.layout.activity_generic

    private val mPresenter by inject<AboutActivityPresenter>()

    @InjectPresenter
    lateinit var presenter: AboutActivityPresenter

    @ProvidePresenter
    fun providePresenter() = mPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbar.setNavigationIcon(R.drawable.close)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onStop() {
        super.onStop()
        release(Screens.ACTIVITY_ABOUT)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> { onBackPressed(); true }
        else -> false
    }

    override fun onBackPressed() = presenter.onBackPressed()

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

        override fun createFragment(screenKey: String?, data: Any?) =
                when (screenKey) {
                    Screens.FRAGMENT_ABOUT -> AboutFragment.newInstance()
                    else -> null
                }
    }
}