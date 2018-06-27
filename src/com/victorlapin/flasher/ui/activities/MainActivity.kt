package com.victorlapin.flasher.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.view.MenuItem
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.victorlapin.flasher.R
import com.victorlapin.flasher.Screens
import com.victorlapin.flasher.presenter.MainActivityPresenter
import com.victorlapin.flasher.ui.fragments.HomeFragment
import com.victorlapin.flasher.ui.fragments.ScheduleFragment
import com.victorlapin.flasher.view.MainActivityView
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject
import org.koin.android.ext.android.release
import ru.terrakok.cicerone.android.SupportAppNavigator

class MainActivity : BaseActivity(), MainActivityView,
        BottomNavigationView.OnNavigationItemSelectedListener,
        BottomNavigationView.OnNavigationItemReselectedListener {

    override val layoutRes = R.layout.activity_main

    private val mPresenter by inject<MainActivityPresenter>()

    @InjectPresenter
    lateinit var presenter: MainActivityPresenter

    @ProvidePresenter
    fun providePresenter() = mPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bottom_bar.setOnNavigationItemSelectedListener(this)
        bottom_bar.setOnNavigationItemReselectedListener(this)
        fab.setOnClickListener {
            presenter.onFabClicked(bottom_bar.selectedItemId)
        }
    }

    override fun onStop() {
        super.onStop()
        release(Screens.ACTIVITY_MAIN)
    }

    override fun onNavigationItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_home -> { presenter.selectHome(); true }
        R.id.action_schedule -> { presenter.selectSchedule(); true }
        else -> false
    }

    override fun onNavigationItemReselected(item: MenuItem) { }

    override val navigator = object : SupportAppNavigator(this, R.id.fragment_container) {
        override fun createActivityIntent(context: Context?, screenKey: String?, data: Any?): Intent? =
                when (screenKey) {
                    Screens.ACTIVITY_SETTINGS-> Intent(context, SettingsActivity::class.java)
                    else -> null
                }

        override fun createFragment(screenKey: String?, data: Any?): Fragment? = when (screenKey) {
            Screens.FRAGMENT_HOME -> HomeFragment.newInstance()
            Screens.FRAGMENT_SCHEDULE -> ScheduleFragment.newInstance()
            else -> null
        }
    }
}