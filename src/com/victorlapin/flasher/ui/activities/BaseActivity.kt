package com.victorlapin.flasher.ui.activities

import android.os.Bundle
import android.os.Handler
import androidx.annotation.StyleRes
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.arellomobile.mvp.MvpAppCompatActivity
import com.victorlapin.flasher.manager.SettingsManager
import com.victorlapin.flasher.setNavigator
import kotlinx.android.synthetic.main.include_toolbar.*
import org.koin.android.ext.android.inject
import org.koin.androidx.scope.bindScope
import org.koin.androidx.scope.currentScope
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.android.support.SupportAppNavigator

abstract class BaseActivity : MvpAppCompatActivity(), LifecycleObserver {
    abstract val layoutRes: Int
    abstract val navigator: SupportAppNavigator?

    private val mNavigationHolder by inject<NavigatorHolder>()
    private val mSettings by inject<SettingsManager>()

    @StyleRes
    private var mCurrentTheme: Int = 0
    private val mHandler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        bindScope(currentScope)
        mCurrentTheme = mSettings.theme
        setTheme(mCurrentTheme)
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(this)
        setContentView(layoutRes)
        setSupportActionBar(toolbar)
        this.setNavigator(mNavigationHolder, navigator)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun checkTheme() {
        val newTheme = mSettings.theme
        if (mCurrentTheme != newTheme) {
            mHandler.post { updateTheme(newTheme) }
        }
    }

    fun updateTheme(@StyleRes newTheme: Int) {
        mCurrentTheme = newTheme
        recreate()
    }
}