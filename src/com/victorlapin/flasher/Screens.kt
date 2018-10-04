package com.victorlapin.flasher

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v4.app.Fragment
import com.victorlapin.flasher.ui.fragments.AboutFragment
import com.victorlapin.flasher.ui.fragments.HomeFragment
import com.victorlapin.flasher.ui.fragments.ScheduleFragment
import com.victorlapin.flasher.ui.fragments.SettingsGlobalFragment
import ru.terrakok.cicerone.android.support.SupportAppScreen

class HomeScreen : SupportAppScreen() {
    init {
        this.screenKey = Const.FRAGMENT_HOME
    }

    override fun getFragment(): Fragment = HomeFragment.newInstance()
}

class ScheduleScreen : SupportAppScreen() {
    init {
        this.screenKey = Const.FRAGMENT_SCHEDULE
    }

    override fun getFragment(): Fragment = ScheduleFragment.newInstance()
}

class SettingsScreen : SupportAppScreen() {
    init {
        this.screenKey = Const.FRAGMENT_SETTINGS
    }

    override fun getFragment(): Fragment = SettingsGlobalFragment.newInstance()
}

class AboutScreen : SupportAppScreen() {
    init {
        this.screenKey = Const.FRAGMENT_ABOUT
    }

    override fun getFragment(): Fragment = AboutFragment.newInstance()
}

class AboutExternalScreen(private val url: String) : SupportAppScreen() {
    init {
        this.screenKey = Const.EXTERNAL_ABOUT
    }

    override fun getActivityIntent(context: Context?): Intent =
            Intent(Intent.ACTION_VIEW, Uri.parse(url))
}