package com.victorlapin.flasher

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.Fragment
import com.victorlapin.flasher.ui.fragments.AboutFragment
import com.victorlapin.flasher.ui.fragments.HomeFragment
import com.victorlapin.flasher.ui.fragments.ScheduleFragment
import com.victorlapin.flasher.ui.fragments.SettingsGlobalFragment
import ru.terrakok.cicerone.androidx.SupportAppScreen

class HomeScreen : SupportAppScreen() {
    override fun getFragment(): Fragment = HomeFragment.newInstance()
}

class ScheduleScreen : SupportAppScreen() {
    override fun getFragment(): Fragment = ScheduleFragment.newInstance()
}

class SettingsScreen : SupportAppScreen() {
    override fun getFragment(): Fragment = SettingsGlobalFragment.newInstance()
}

class AboutScreen : SupportAppScreen() {
    override fun getFragment(): Fragment = AboutFragment.newInstance()
}

class AboutExternalScreen(private val url: String) : SupportAppScreen() {
    override fun getActivityIntent(context: Context?): Intent =
            Intent(Intent.ACTION_VIEW, Uri.parse(url))
}