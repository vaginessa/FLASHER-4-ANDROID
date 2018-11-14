package com.victorlapin.flasher.ui.fragments

import android.Manifest
import android.os.Bundle
import android.widget.Toast
import androidx.interpolator.view.animation.FastOutLinearInInterpolator
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Fade
import androidx.transition.TransitionManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.files.FileFilter
import com.afollestad.materialdialogs.files.fileChooser
import com.afollestad.materialdialogs.files.folderChooser
import com.afollestad.materialdialogs.input.input
import com.afollestad.materialdialogs.list.listItemsMultiChoice
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.tbruyelle.rxpermissions2.RxPermissions
import com.victorlapin.flasher.*
import com.victorlapin.flasher.manager.ResourcesManager
import com.victorlapin.flasher.manager.ServicesManager
import com.victorlapin.flasher.model.EventArgs
import com.victorlapin.flasher.model.database.entity.Chain
import com.victorlapin.flasher.model.database.entity.Command
import com.victorlapin.flasher.presenter.BaseHomeFragmentPresenter
import com.victorlapin.flasher.presenter.DefaultHomePresenter
import com.victorlapin.flasher.ui.Biometric
import com.victorlapin.flasher.ui.adapters.HomeAdapter
import com.victorlapin.flasher.view.HomeFragmentView
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.include_progress.*
import kotlinx.android.synthetic.main.include_toolbar_center.*
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.io.File

open class HomeFragment : BaseFragment(), HomeFragmentView {
    override val layoutRes = R.layout.fragment_home
    override val scopeName = Const.FRAGMENT_HOME

    @InjectPresenter
    lateinit var presenter: BaseHomeFragmentPresenter

    @ProvidePresenter
    open fun providePresenter(): BaseHomeFragmentPresenter = get<DefaultHomePresenter>()

    private val mServices by inject<ServicesManager>()
    protected val mResources by inject<ResourcesManager>()

    private val mRxPermissions by lazy {
        RxPermissions(this)
    }

    private lateinit var mWipePartitions: List<String>
    private lateinit var mBackupPartitions: List<String>

    private val mChainId by lazy {
        arguments!!.getLong(ARG_CHAIN_ID)
    }

    private var mIsRebootInProgress = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        savedInstanceState?.let {
            mIsRebootInProgress = it.getBoolean(ARG_REBOOT_IN_PROGRESS)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(ARG_REBOOT_IN_PROGRESS, mIsRebootInProgress)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mWipePartitions = mResources.getStringList(R.array.wipe_partitions)
        mBackupPartitions = mResources.getStringList(R.array.backup_partitions)

        val homeAdapter = HomeAdapter(
                resources = get(),
                changeTypeListener = { presenter.onCommandTypeChanged(it) },
                argsClickListener = { presenter.onArgumentsClicked(it) },
                itemInsertListener = { list.smoothScrollToPosition(it) }
        )
        list.apply {
            layoutManager = LinearLayoutManager(context)
            itemAnimator = DefaultItemAnimator()
            setHasFixedSize(true)
            adapter = homeAdapter
        }
        toolbar_title.text = mResources.getString(R.string.action_home)
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
                (recyclerView.adapter as HomeAdapter).
                        moveItems(dragged.adapterPosition, target.adapterPosition)
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
                val adapter = recyclerView.adapter as HomeAdapter
                adapter.onMoveFinished()
                val newItems = adapter.getItems()
                presenter.onOrderChanged(newItems)
            }
        }
        ItemTouchHelper(swipeCallback).attachToRecyclerView(list)
    }

    override fun setData(commands: List<Command>) {
        list.post {
            (list.adapter as HomeAdapter).setData(commands)
            empty_view?.visible(commands.isEmpty())
        }
    }

    override fun showWipeDialog(command: Command) {
        val indices = if (command.arg1 != null) {
            val preselected = command.arg1!!.toArray()
            val i = arrayListOf<Int>()
            preselected.forEach { i.add(mWipePartitions.indexOf(it)) }
            i.toIntArray()
        } else intArrayOf()
        MaterialDialog(context!!)
                .title(res = R.string.command_wipe)
                .listItemsMultiChoice(items = mWipePartitions,
                        initialSelection = indices) { _, _, items ->
                    command.arg1 = items.toSet().toString().flatten()
                    presenter.onCommandUpdated(command)
                }
                .positiveButton(res = android.R.string.ok)
                .negativeButton(res = android.R.string.cancel)
                .show()
    }

    override fun showBackupDialog(command: Command) {
        val indices = if (command.arg1 != null) {
            val preselected = command.arg1!!.toArray()
            val i = arrayListOf<Int>()
            preselected.forEach { i.add(mBackupPartitions.indexOf(it)) }
            i.toIntArray()
        } else intArrayOf()
        MaterialDialog(context!!)
                .title(res = R.string.command_backup)
                .listItemsMultiChoice(items = mBackupPartitions,
                        initialSelection = indices) { _, _, items ->
                    command.arg1 = items.toSet().toString().flatten()
                    presenter.onCommandUpdated(command)
                }
                .positiveButton(res = android.R.string.ok)
                .negativeButton(res = android.R.string.cancel)
                .show()
    }

    override fun showFlashFileDialog(command: Command, startPath: String?) {
        mRxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .firstOrError()
                .doOnSuccess { granted ->
                    if (granted) {
                        val initialPath = startPath ?: Const.FALLBACK_FOLDER
                        val filter: FileFilter = { it.isDirectory ||
                                it.name.endsWith(".zip", true) }
                        MaterialDialog(context!!)
                                .fileChooser(initialDirectory = File(initialPath),
                                        filter = filter,
                                        emptyTextRes = R.string.folder_empty) { _, file ->
                                    command.arg1 = file.absolutePath
                                    command.arg2 = file.parent
                                    presenter.onCommandUpdated(command)
                                    presenter.onFileSelected(file)
                                }
                                .positiveButton(res = android.R.string.ok)
                                .negativeButton(res = android.R.string.cancel)
                                .show()
                    } else {
                        MaterialDialog(context!!)
                                .title(res = R.string.app_name)
                                .message(res = R.string.permission_denied_storage)
                                .positiveButton(res = android.R.string.ok)
                                .show()
                    }
                }
                .doOnError { Timber.e(it) }
                .subscribe()
    }

    override fun showEditMaskDialog(command: Command) {
        MaterialDialog(context!!)
                .title(res = R.string.enter_mask)
                .input(prefill = command.arg1) { _, input ->
                    command.arg1 = if (input.isEmpty()) null else input.toString()
                    presenter.onCommandUpdated(command)
                }
                .positiveButton(res = android.R.string.ok)
                .negativeButton(res = android.R.string.cancel)
                .show()
    }

    override fun showSelectFolderDialog(command: Command, startPath: String?) {
        mRxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .firstOrError()
                .doOnSuccess { granted ->
                    if (granted) {
                        val initialPath = startPath ?: Const.FALLBACK_FOLDER
                        MaterialDialog(context!!)
                                .folderChooser(initialDirectory = File(initialPath),
                                        emptyTextRes = R.string.folder_empty) { _, folder ->
                                    command.arg2 = folder.absolutePath
                                    presenter.onCommandUpdated(command)
                                    presenter.onFileSelected(folder)
                                }
                                .positiveButton(res = android.R.string.ok)
                                .negativeButton(res = android.R.string.cancel)
                                .show()
                    } else {
                        MaterialDialog(context!!)
                                .title(res = R.string.app_name)
                                .message(res = R.string.permission_denied_storage)
                                .positiveButton(res = android.R.string.ok)
                                .show()
                    }
                }
                .doOnError { Timber.e(it) }
                .subscribe()
    }

    override fun showDeletedSnackbar(command: Command) {
        snackbar.show(
                messageId = R.string.command_deleted,
                actionId = R.string.action_undo,
                actionClick = {
                    presenter.onUndoDelete(command)
                    snackbar.dismiss()
                }
        )
    }

    override fun showInfoSnackbar(args: EventArgs) {
        snackbar.show(
                messageText = args.message?.replace("\n", "")?.trim(),
                messageId = args.messageId
        )
    }

    override fun showRebootSnackbar() {
        snackbar.show(
                messageId = R.string.reboot,
                actionId = R.string.action_reboot,
                actionClick = {
                    snackbar.dismiss()
                    mIsRebootInProgress = true
                    presenter.onRebootRequested()
                },
                isIndefinite = true
        )
    }

    override fun askFingerprint() {
        if (mIsRebootInProgress) {
            if (mServices.isFingerprintAvailable()) {
                Biometric.askFingerprint(
                        activity = activity!!,
                        title = R.string.fingerprint_reboot_title,
                        successListener = { presenter.reboot(); mIsRebootInProgress = false },
                        cancelListener = { mIsRebootInProgress = false }
                )
            } else {
                presenter.reboot()
                mIsRebootInProgress = false
            }
        }
    }

    override fun showExportDialog() {
        mRxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .firstOrError()
                .doOnSuccess { granted ->
                    if (granted) {
                        MaterialDialog(context!!)
                                .title(res = R.string.enter_file_name)
                                .input { _, input ->
                                    val fileName = if (input.endsWith(".json", true))
                                        input.toString() else "$input.json"
                                    presenter.exportCommands(fileName)
                                }
                                .positiveButton(res = android.R.string.ok)
                                .negativeButton(res = android.R.string.cancel)
                                .show()
                    } else {
                        MaterialDialog(context!!)
                                .title(res = R.string.app_name)
                                .message(res = R.string.permission_denied_storage)
                                .positiveButton(res = android.R.string.ok)
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
                        val filter: FileFilter = { it.isDirectory ||
                                it.name.endsWith(".json", true) }
                        MaterialDialog(context!!)
                                .fileChooser(initialDirectory = File(Const.APP_FOLDER),
                                        filter = filter,
                                        emptyTextRes = R.string.folder_empty) { _, file ->
                                    presenter.importCommands(file.absolutePath)
                                }
                                .positiveButton(res = android.R.string.ok)
                                .negativeButton(res = android.R.string.cancel)
                                .show()
                    } else {
                        MaterialDialog(context!!)
                                .title(res = R.string.app_name)
                                .message(res = R.string.permission_denied_storage)
                                .positiveButton(res = android.R.string.ok)
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
        val navFragment = BottomNavigationDrawerFragment.newInstance(
                selectedId = selectedId,
                clickListener = { presenter.onNavigationClicked(it) }
        )
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
        private const val ARG_REBOOT_IN_PROGRESS = "ARG_REBOOT_IN_PROGRESS"
    }
}