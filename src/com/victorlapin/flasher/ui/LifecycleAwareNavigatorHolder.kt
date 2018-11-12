package com.victorlapin.flasher.ui

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.NavigatorHolder

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