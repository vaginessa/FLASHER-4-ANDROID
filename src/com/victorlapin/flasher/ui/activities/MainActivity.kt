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
import com.victorlapin.flasher.model.database.entity.Chain
import com.victorlapin.flasher.presenter.MainActivityPresenter
import com.victorlapin.flasher.ui.fragments.HomeFragment
import com.victorlapin.flasher.view.MainActivityView
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject
import org.koin.android.ext.android.releaseContext
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
        bottom_bar.enableAnimation(false)
        bottom_bar.enableShiftingMode(false)
        bottom_bar.enableItemShiftingMode(false)
        bottom_bar.onNavigationItemSelectedListener = this
        bottom_bar.setOnNavigationItemReselectedListener(this)
        fab.setOnClickListener {
            val id = bottom_bar.menu.getItem(bottom_bar.currentItem).itemId
            presenter.onFabClicked(id)
        }
    }

    override fun onStop() {
        super.onStop()
        releaseContext(Screens.ACTIVITY_MAIN)
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
            Screens.FRAGMENT_HOME -> HomeFragment.newInstance(Chain.DEFAULT_ID)
            Screens.FRAGMENT_SCHEDULE -> HomeFragment.newInstance(Chain.SCHEDULE_ID)
            else -> null
        }
    }
}