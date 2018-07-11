package com.victorlapin.flasher.view

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.victorlapin.flasher.model.EventArgs
import com.victorlapin.flasher.model.database.entity.Command

interface HomeFragmentView : MvpView {
    fun setData(commands: List<Command>)
    @StateStrategyType(SkipStrategy::class)
    fun showWipeDialog(command: Command)
    @StateStrategyType(SkipStrategy::class)
    fun showBackupDialog(command: Command)
    @StateStrategyType(SkipStrategy::class)
    fun showFlashFileDialog(command: Command, startPath: String?)
    @StateStrategyType(SkipStrategy::class)
    fun showEditMaskDialog(command: Command)
    @StateStrategyType(SkipStrategy::class)
    fun showSelectFolderDialog(command: Command, startPath: String?)
    @StateStrategyType(SkipStrategy::class)
    fun showDeletedSnackbar(command: Command)
    @StateStrategyType(SkipStrategy::class)
    fun showInfoSnackbar(args: EventArgs)
    @StateStrategyType(SkipStrategy::class)
    fun showRebootSnackbar()
    @StateStrategyType(SkipStrategy::class)
    fun showExportDialog()
    @StateStrategyType(SkipStrategy::class)
    fun showImportDialog()
    @StateStrategyType(SkipStrategy::class)
    fun showInfoToast(message: String)
    @StateStrategyType(SkipStrategy::class)
    fun showSelectTimeDialog(defHourOfDay: Int, defMinute: Int)
    @StateStrategyType(SkipStrategy::class)
    fun showSelectIntervalDialog(defInterval: Int)
    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showNextRun(hasNext: Boolean, nextRun: Long)
    fun toggleProgress(isVisible: Boolean)
}