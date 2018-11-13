package com.victorlapin.flasher.ui.activities

import android.os.Bundle
import android.os.Handler
import androidx.annotation.StyleRes
import com.arellomobile.mvp.MvpAppCompatActivity
import com.victorlapin.flasher.manager.SettingsManager
import com.victorlapin.flasher.setNavigator
import kotlinx.android.synthetic.main.include_toolbar.*
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.android.inject
import org.koin.core.scope.Scope
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.androidx.SupportAppNavigator

abstract class BaseActivity : MvpAppCompatActivity() {
    abstract val layoutRes: Int
    abstract val navigator: SupportAppNavigator?
    abstract val scopeName: String

    private lateinit var mScope: Scope
    private val mNavigationHolder by inject<NavigatorHolder>()
    private val mSettings by inject<SettingsManager>()

    @StyleRes
    private var mCurrentTheme: Int = 0
    private val mHandler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        mScope = getKoin().createScope(scopeName)
        mCurrentTheme = mSettings.theme
        setTheme(mCurrentTheme)
        super.onCreate(savedInstanceState)
        setContentView(layoutRes)
        setSupportActionBar(toolbar)
        this.setNavigator(mNavigationHolder, navigator)
    }

    override fun onResume() {
        super.onResume()

        val newTheme = mSettings.theme
        if (mCurrentTheme != newTheme) {
            mHandler.post { updateTheme(newTheme) }
        }
    }

    fun updateTheme(@StyleRes newTheme: Int) {
        mCurrentTheme = newTheme
        recreate()
    }

    override fun onStop() {
        super.onStop()
        mScope.close()
    }
}