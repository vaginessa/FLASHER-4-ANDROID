package com.victorlapin.flasher.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Menu
import android.view.MenuItem
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.victorlapin.flasher.R
import com.victorlapin.flasher.Screens
import com.victorlapin.flasher.presenter.MainActivityPresenter
import com.victorlapin.flasher.ui.fragments.BottomNavigationDrawerFragment
import com.victorlapin.flasher.ui.fragments.HomeFragment
import com.victorlapin.flasher.ui.fragments.ScheduleFragment
import com.victorlapin.flasher.view.MainActivityView
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject
import org.koin.android.ext.android.release
import ru.terrakok.cicerone.android.SupportAppNavigator

class MainActivity : BaseActivity(), MainActivityView {
    override val layoutRes = R.layout.activity_main

    private val mPresenter by inject<MainActivityPresenter>()

    @InjectPresenter
    lateinit var presenter: MainActivityPresenter

    @ProvidePresenter
    fun providePresenter() = mPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(bottom_app_bar)
        fab.setOnClickListener { presenter.onFabClicked() }
    }

    override fun onStop() {
        super.onStop()
        release(Screens.ACTIVITY_MAIN)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> { presenter.selectNavigation(); true }
        R.id.action_settings -> { presenter.selectSettings(); true }
        else -> false
    }

    override fun showNavigationFragment(selectedId: Int) {
        var disposable: Disposable? = null
        val navFragment = BottomNavigationDrawerFragment.newInstance(selectedId)
        disposable = navFragment.clickEvent
                .subscribe {
                    presenter.onNavigationClicked(it)
                    disposable?.dispose()
                }
        navFragment.show(supportFragmentManager,
                BottomNavigationDrawerFragment::class.java.simpleName)
    }

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