package com.victorlapin.flasher.view

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.victorlapin.flasher.model.EventArgs

interface ScriptTileServiceView : MvpView {
    @StateStrategyType(SkipStrategy::class)
    fun showInfoToast(args: EventArgs)
    @StateStrategyType(SkipStrategy::class)
    fun showRebootDialog()
}