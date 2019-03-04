package com.victorlapin.flasher.view

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

@StateStrategyType(SkipStrategy::class)
interface MainActivityView : MvpView {
    fun askFingerprint()
    fun cancelFingerprint()
}