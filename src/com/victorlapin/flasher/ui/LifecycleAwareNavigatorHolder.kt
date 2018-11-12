package com.victorlapin.flasher.ui

import android.arch.lifecycle.LifecycleOwner
import ru.terrakok.cicerone.Navigator
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.OnLifecycleEvent
import ru.terrakok.cicerone.NavigatorHolder
import android.arch.lifecycle.LifecycleObserver

class LifecycleAwareNavigatorHolder(
        private val navigatorHolder: NavigatorHolder
) : LifecycleObserver {
    private var navigator: Navigator? = null

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        navigatorHolder.setNavigator(navigator)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        navigatorHolder.removeNavigator()
    }

    fun setNavigator(navigator: Navigator?) {
        this.navigator = navigator
    }

    fun register(lifecycleOwner: LifecycleOwner) {
        lifecycleOwner.lifecycle.addObserver(this)
    }
}