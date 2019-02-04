package com.victorlapin.flasher.ui

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.NavigatorHolder

class LifecycleAwareNavigatorHolder(
    lifecycle: Lifecycle,
    private val mNavigatorHolder: NavigatorHolder,
    private val mNavigator: Navigator?
) : LifecycleObserver {
    init {
        lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        mNavigatorHolder.setNavigator(mNavigator)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        mNavigatorHolder.removeNavigator()
    }
}