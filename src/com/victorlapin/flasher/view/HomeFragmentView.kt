package com.victorlapin.flasher.view

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.victorlapin.flasher.model.database.entity.Command

interface HomeFragmentView : MvpView {
    fun setData(commands: List<Command>)
    @StateStrategyType(SkipStrategy::class)
    fun showWipeDialog(command: Command)
    @StateStrategyType(SkipStrategy::class)
    fun showBackupDialog(command: Command)
    @StateStrategyType(SkipStrategy::class)
    fun showFlashDialog(command: Command)
}