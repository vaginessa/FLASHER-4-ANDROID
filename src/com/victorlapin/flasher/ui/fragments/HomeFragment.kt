package com.victorlapin.flasher.ui.fragments

import android.Manifest
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.transition.Fade
import android.support.transition.TransitionManager
import android.support.v4.view.animation.FastOutLinearInInterpolator
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.folderselector.FileChooserDialog
import com.afollestad.materialdialogs.folderselector.FolderChooserDialog
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.tbruyelle.rxpermissions2.RxPermissions
import com.victorlapin.flasher.*
import com.victorlapin.flasher.manager.ResourcesManager
import com.victorlapin.flasher.model.EventArgs
import com.victorlapin.flasher.model.database.entity.Chain
import com.victorlapin.flasher.model.database.entity.Command
import com.victorlapin.flasher.presenter.BaseHomeFragmentPresenter
import com.victorlapin.flasher.presenter.DefaultHomePresenter
import com.victorlapin.flasher.ui.adapters.HomeAdapter
import com.victorlapin.flasher.ui.adapters.LinearLayoutManagerWrapper
import com.victorlapin.flasher.view.HomeFragmentView
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.include_progress.*
import kotlinx.android.synthetic.main.include_toolbar.*
import org.koin.android.ext.android.inject
import org.koin.android.ext.android.release
import timber.log.Timber
import java.io.File

open class HomeFragment : BaseFragment(), HomeFragmentView {
    override val layoutRes = R.layout.fragment_home

    private val mDefaultPresenter by inject<DefaultHomePresenter>()

    @InjectPresenter
    lateinit var presenter: BaseHomeFragmentPresenter

    @ProvidePresenter
    open fun providePresenter(): BaseHomeFragmentPresenter = mDefaultPresenter

    private var mDisposable: CompositeDisposable? = null
    private val mNavEventsDisposable = CompositeDisposable()

    private val mAdapter by inject<HomeAdapter>()

    private val mResources by inject<ResourcesManager>()

    private val mRxPermissions by lazy {
        RxPermissions(this)
    }

    private lateinit var mWipePartitions: List<String>
    private lateinit var mBackupPartitions: List<String>

    private val mChainId by lazy {
        arguments!!.getLong(ARG_CHAIN_ID)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mDisposable = CompositeDisposable()
        mWipePartitions = mResources.getStringList(R.array.wipe_partitions)
        mBackupPartitions = mResources.getStringList(R.array.backup_partitions)

        setupEvents()
        list.apply {
            layoutManager = LinearLayoutManagerWrapper(context)
            itemAnimator = DefaultItemAnimator()
            setHasFixedSize(true)
            adapter = mAdapter
        }
        toolbar.setTitle(R.string.action_home)
        fab.setOnClickListener { presenter.onFabClicked() }

        bottom_app_bar.inflateMenu(R.menu.fragment_home)
        bottom_app_bar.setNavigationOnClickListener { presenter.selectNavigation() }
        bottom_app_bar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_build -> { presenter.buildAndDeploy(mChainId); true }
                R.id.action_export -> { presenter.onExportClicked(); true }
                R.id.action_import -> { presenter.onImportClicked(); true }
                R.id.action_settings -> { presenter.selectSettings(); true }
                else -> false
            }
        }

        val swipeCallback = object : ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP or ItemTouchHelper.DOWN,
                ItemTouchHelper.START or ItemTouchHelper.END) {

            override fun isLongPressDragEnabled(): Boolean = true

            override fun isItemViewSwipeEnabled(): Boolean = true

            override fun onMove(recyclerView: RecyclerView, dragged: RecyclerView.ViewHolder,
                                target: RecyclerView.ViewHolder): Boolean {
                mAdapter.moveItems(dragged.adapterPosition, target.adapterPosition)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val id = (viewHolder as HomeAdapter.ViewHolder).itemId
                presenter.onCommandSwiped(id)
            }

            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                    (viewHolder as HomeAdapter.ViewHolder).onSelected()
                }
                super.onSelectedChanged(viewHolder, actionState)
            }

            override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                super.clearView(recyclerView, viewHolder)

                (viewHolder as HomeAdapter.ViewHolder).onCleared()
                mAdapter.onMoveFinished()
                val newItems = mAdapter.getItems()
                presenter.onOrderChanged(newItems)
            }
        }
        ItemTouchHelper(swipeCallback).attachToRecyclerView(list)
    }

    override fun onStop() {
        super.onStop()
        release(Screens.FRAGMENT_HOME)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mDisposable?.clear()
        mDisposable = null
    }

    private fun setupEvents() {
        mAdapter.changeTypeEvent
                .subscribe { presenter.onCommandTypeChanged(it) }
                .addTo(mDisposable!!)
        mAdapter.argsClickEvent
                .subscribe { presenter.onArgumentsClicked(it) }
                .addTo(mDisposable!!)
        mAdapter.itemInsertEvent
                .subscribe { list.smoothScrollToPosition(it) }
                .addTo(mDisposable!!)
    }

    override fun setData(commands: List<Command>) {
        list.post {
            mAdapter.setData(commands)
        }
    }

    override fun showWipeDialog(command: Command) {
        val indices = if (command.arg1 != null) {
            val preselected = command.arg1!!.toArray()
            val i = arrayListOf<Int>()
            preselected.forEach { i.add(mWipePartitions.indexOf(it)) }
            i.toTypedArray()
        } else null
        MaterialDialog.Builder(context!!)
                .title(R.string.command_wipe)
                .items(mWipePartitions)
                .itemsCallbackMultiChoice(indices) { _, _, items ->
                    command.arg1 = items.toSet().toString().flatten()
                    presenter.onCommandUpdated(command)
                    return@itemsCallbackMultiChoice true
                }
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel)
                .show()
    }

    override fun showBackupDialog(command: Command) {
        val indices = if (command.arg1 != null) {
            val preselected = command.arg1!!.toArray()
            val i = arrayListOf<Int>()
            preselected.forEach { i.add(mBackupPartitions.indexOf(it)) }
            i.toTypedArray()
        } else null
        MaterialDialog.Builder(context!!)
                .title(R.string.command_backup)
                .items(mBackupPartitions)
                .itemsCallbackMultiChoice(indices) { _, _, items ->
                    command.arg1 = items.toSet().toString().flatten()
                    presenter.onCommandUpdated(command)
                    return@itemsCallbackMultiChoice true
                }
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel)
                .show()
    }

    override fun showFlashFileDialog(command: Command, startPath: String?) {
        mRxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .firstOrError()
                .doOnSuccess { granted ->
                    if (granted) {
                        FileChooserDialog.Builder(context!!)
                                .initialPath(startPath)
                                .extensionsFilter(".zip")
                                .callback(object : FileChooserDialog.FileCallback {
                                    override fun onFileChooserDismissed(dialog: FileChooserDialog) { }
                                    override fun onFileSelection(dialog: FileChooserDialog, file: File) {
                                        command.arg1 = file.absolutePath
                                        command.arg2 = file.parent
                                        presenter.onCommandUpdated(command)
                                        presenter.onFileSelected(file)
                                    }
                                })
                                .show(activity!!)
                    } else {
                        MaterialDialog.Builder(context!!)
                                .title(R.string.app_name)
                                .content(R.string.permission_denied_storage)
                                .positiveText(android.R.string.ok)
                                .show()
                    }
                }
                .doOnError { Timber.e(it) }
                .subscribe()
    }

    override fun showEditMaskDialog(command: Command) {
        MaterialDialog.Builder(context!!)
                .title(R.string.enter_mask)
                .input("", command.arg1, true) { _, input ->
                    command.arg1 = if (input.isEmpty()) null else input.toString()
                    presenter.onCommandUpdated(command)
                }
                .negativeText(android.R.string.cancel)
                .show()
    }

    override fun showSelectFolderDialog(command: Command, startPath: String?) {
        mRxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .firstOrError()
                .doOnSuccess { granted ->
                    if (granted) {
                        FolderChooserDialog.Builder()
                                .initialPath(startPath)
                                .callback(object : FolderChooserDialog.FolderCallback {
                                    override fun onFolderChooserDismissed(dialog: FolderChooserDialog) { }
                                    override fun onFolderSelection(dialog: FolderChooserDialog, folder: File) {
                                        command.arg2 = folder.absolutePath
                                        presenter.onCommandUpdated(command)
                                        presenter.onFileSelected(folder)
                                    }
                                })
                                .show(activity!!)
                    } else {
                        MaterialDialog.Builder(context!!)
                                .title(R.string.app_name)
                                .content(R.string.permission_denied_storage)
                                .positiveText(android.R.string.ok)
                                .show()
                    }
                }
                .doOnError { Timber.e(it) }
                .subscribe()
    }

    override fun showDeletedSnackbar(command: Command) {
        Snackbar.make(coordinator, R.string.command_deleted, Snackbar.LENGTH_LONG)
                .setAction(R.string.action_undo) { presenter.onUndoDelete(command) }
                .adjustLayout()
                .show()
    }

    override fun showInfoSnackbar(args: EventArgs) {
        args.message?.let {
            Snackbar.make(coordinator, it.replace("\n", "").trim(),
                    Snackbar.LENGTH_LONG)
                    .adjustLayout()
                    .show()
            return
        }
        args.messageId?.let {
            Snackbar.make(coordinator, mResources.getString(it),
                    Snackbar.LENGTH_LONG)
                    .adjustLayout()
                    .show()
        }
    }

    override fun showRebootSnackbar() {
        Snackbar.make(coordinator, R.string.reboot, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.action_reboot) { presenter.reboot() }
                .adjustLayout()
                .show()
    }

    override fun showExportDialog() {
        mRxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .firstOrError()
                .doOnSuccess { granted ->
                    if (granted) {
                        MaterialDialog.Builder(context!!)
                                .title(R.string.enter_file_name)
                                .input(null, null, false) { _, input ->
                                    val fileName = if (input.endsWith(".json", true))
                                        input.toString() else "$input.json"
                                    presenter.exportCommands(fileName)
                                }
                                .negativeText(android.R.string.cancel)
                                .show()
                    } else {
                        MaterialDialog.Builder(context!!)
                                .title(R.string.app_name)
                                .content(R.string.permission_denied_storage)
                                .positiveText(android.R.string.ok)
                                .show()
                    }
                }
                .doOnError { Timber.e(it) }
                .subscribe()
    }

    override fun showImportDialog() {
        mRxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .firstOrError()
                .doOnSuccess { granted ->
                    if (granted) {
                        FileChooserDialog.Builder(context!!)
                                .initialPath(Const.APP_FOLDER)
                                .extensionsFilter(".json")
                                .callback(object : FileChooserDialog.FileCallback {
                                    override fun onFileChooserDismissed(dialog: FileChooserDialog) { }
                                    override fun onFileSelection(dialog: FileChooserDialog, file: File) {
                                        presenter.importCommands(file.absolutePath)
                                    }
                                })
                                .show(activity!!)
                    } else {
                        MaterialDialog.Builder(context!!)
                                .title(R.string.app_name)
                                .content(R.string.permission_denied_storage)
                                .positiveText(android.R.string.ok)
                                .show()
                    }
                }
                .doOnError { Timber.e(it) }
                .subscribe()
    }

    override fun showInfoToast(message: String) {
        list.post {
            Toast.makeText(context!!, message, Toast.LENGTH_LONG).show()
        }
    }

    override fun showSelectTimeDialog(defHourOfDay: Int, defMinute: Int) {

    }

    override fun showSelectIntervalDialog(defInterval: Int) {

    }

    override fun showNextRun(hasNext: Boolean, nextRun: Long) {

    }

    override fun toggleProgress(isVisible: Boolean) {
        fab.post {
            val transition = Fade().setInterpolator(FastOutLinearInInterpolator())
            TransitionManager.beginDelayedTransition(coordinator, transition)
            progress_bar_layout.visible(isVisible)
            fab.visible(!isVisible)
        }
    }

    override fun showNavigationFragment(selectedId: Int) {
        val navFragment = BottomNavigationDrawerFragment.newInstance(selectedId)
        navFragment.clickEvent
                .subscribe {
                    presenter.onNavigationClicked(it)
                }
                .addTo(mNavEventsDisposable)
        navFragment.dismissEvent
                .subscribe {
                    mNavEventsDisposable.clear()
                }
                .addTo(mNavEventsDisposable)
        navFragment.show(activity!!.supportFragmentManager,
                BottomNavigationDrawerFragment::class.java.simpleName)
    }

    companion object {
        fun newInstance(): HomeFragment {
            val fragment = HomeFragment()
            val args = Bundle()
            args.putLong(ARG_CHAIN_ID, Chain.DEFAULT_ID)
            fragment.arguments = args
            return fragment
        }

        const val ARG_CHAIN_ID = "ARG_CHAIN_ID"
    }
}