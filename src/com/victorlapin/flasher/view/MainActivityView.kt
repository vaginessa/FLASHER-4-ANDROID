package com.victorlapin.flasher.view

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

interface MainActivityView : MvpView {
    @StateStrategyType(SkipStrategy::class)
    fun showNavigationFragment(selectedId: Int)
}