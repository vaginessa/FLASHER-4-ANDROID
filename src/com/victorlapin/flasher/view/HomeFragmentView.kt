package com.victorlapin.flasher.view

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.victorlapin.flasher.model.EventArgs
import com.victorlapin.flasher.model.database.entity.Command

interface HomeFragmentView : MvpView {
    fun setData(commands: List<Command>, isFirstRun: Boolean)
    @StateStrategyType(SkipStrategy::class)
    fun showWipeDialog(command: Command)
    @StateStrategyType(SkipStrategy::class)
    fun showBackupDialog(command: Command)
    @StateStrategyType(SkipStrategy::class)
    fun showFlashFileDialog(command: Command, startPath: String?)
    @StateStrategyType(SkipStrategy::class)
    fun showDeletedSnackbar(command: Command)
    @StateStrategyType(SkipStrategy::class)
    fun showInfoSnackbar(args: EventArgs)
    @StateStrategyType(SkipStrategy::class)
    fun showRebootSnackbar()
}