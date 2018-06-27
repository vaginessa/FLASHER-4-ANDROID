package com.victorlapin.flasher.ui.fragments

import android.Manifest
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.folderselector.FileChooserDialog
import com.afollestad.materialdialogs.folderselector.FolderChooserDialog
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.tbruyelle.rxpermissions2.RxPermissions
import com.victorlapin.flasher.Const
import com.victorlapin.flasher.R
import com.victorlapin.flasher.Screens
import com.victorlapin.flasher.addTo
import com.victorlapin.flasher.manager.ResourcesManager
import com.victorlapin.flasher.model.EventArgs
import com.victorlapin.flasher.model.database.entity.Chain
import com.victorlapin.flasher.model.database.entity.Command
import com.victorlapin.flasher.presenter.DefaultHomePresenter
import com.victorlapin.flasher.presenter.HomeFragmentPresenter
import com.victorlapin.flasher.ui.adapters.HomeAdapter
import com.victorlapin.flasher.view.HomeFragmentView
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.include_toolbar.*
import org.koin.android.ext.android.inject
import org.koin.android.ext.android.release
import java.io.File

open class HomeFragment : BaseFragment(), HomeFragmentView {
    override val layoutRes = R.layout.fragment_home

    private val mDefaultPresenter by inject<DefaultHomePresenter>()

    @InjectPresenter
    lateinit var presenter: HomeFragmentPresenter

    @ProvidePresenter
    open fun providePresenter(): HomeFragmentPresenter = mDefaultPresenter

    private val mDisposable = CompositeDisposable()

    private val mAdapter by inject<HomeAdapter>()

    private val mResources by inject<ResourcesManager>()

    private val mRxPermissions by lazy {
        RxPermissions(activity!!)
    }

    private lateinit var mWipePartitions: List<String>
    private lateinit var mBackupPartitions: List<String>

    private val mChainId by lazy {
        arguments!!.getLong(ARG_CHAIN_ID)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mWipePartitions = mResources.getStringList(R.array.wipe_partitions)
        mBackupPartitions = mResources.getStringList(R.array.backup_partitions)

        setupEvents()
        list.apply {
            layoutManager = LinearLayoutManager(context)
            itemAnimator = DefaultItemAnimator()
            setHasFixedSize(true)
            adapter = mAdapter
        }
        toolbar.setTitle(R.string.title_home)
        toolbar.inflateMenu(R.menu.fragment_home)
        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_build -> { presenter.buildAndDeploy(mChainId); true }
                R.id.action_export -> { presenter.onExportClicked(); true }
                R.id.action_import -> { presenter.onImportClicked(); true }
                R.id.action_settings -> { presenter.onSettingsClicked(); true }
                else -> false
            }
        }

        val swipeCallback = object : ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.START or ItemTouchHelper.END) {
            override fun isLongPressDragEnabled(): Boolean = false

            override fun isItemViewSwipeEnabled(): Boolean = true

            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                                target: RecyclerView.ViewHolder): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val id = (viewHolder as HomeAdapter.ViewHolder).itemId
                presenter.onCommandSwiped(id)
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
        mDisposable.dispose()
    }

    private fun setupEvents() {
        mAdapter.changeTypeEvent
                .subscribe { presenter.onCommandTypeChanged(it) }
                .addTo(mDisposable)
        mAdapter.argsClickEvent
                .subscribe { presenter.onArgumentsClicked(it) }
                .addTo(mDisposable)
    }

    override fun setData(commands: List<Command>, isFirstRun: Boolean) {
        list.post {
            mAdapter.setData(commands)
            if (!isFirstRun && commands.isNotEmpty()) {
                list.scrollToPosition(commands.size - 1)
            }
        }
    }

    override fun showWipeDialog(command: Command) {
        val indices = if (command.arg1 != null) {
            val preselected = toArray(command.arg1!!)
            val i = arrayListOf<Int>()
            preselected.forEach { i.add(mWipePartitions.indexOf(it)) }
            i.toTypedArray()
        } else null
        MaterialDialog.Builder(context!!)
                .title(R.string.command_wipe)
                .items(mWipePartitions)
                .itemsCallbackMultiChoice(indices) { _, _, items ->
                    command.arg1 = flatten(items.toSet().toString())
                    presenter.onCommandUpdated(command)
                    return@itemsCallbackMultiChoice true
                }
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel)
                .show()
    }

    override fun showBackupDialog(command: Command) {
        val indices = if (command.arg1 != null) {
            val preselected = toArray(command.arg1!!)
            val i = arrayListOf<Int>()
            preselected.forEach { i.add(mBackupPartitions.indexOf(it)) }
            i.toTypedArray()
        } else null
        MaterialDialog.Builder(context!!)
                .title(R.string.command_backup)
                .items(mBackupPartitions)
                .itemsCallbackMultiChoice(indices) { _, _, items ->
                    command.arg1 = flatten(items.toSet().toString())
                    presenter.onCommandUpdated(command)
                    return@itemsCallbackMultiChoice true
                }
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel)
                .show()
    }

    override fun showFlashFileDialog(command: Command, startPath: String?) {
        mRxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe { granted ->
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
                .addTo(mDisposable)
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
                .subscribe { granted ->
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
                .addTo(mDisposable)
    }

    override fun showDeletedSnackbar(command: Command) {
        Snackbar.make(coordinator, R.string.command_deleted, Snackbar.LENGTH_LONG)
                .setAction(R.string.action_undo) { presenter.onUndoDelete(command) }
                .show()
    }

    override fun showInfoSnackbar(args: EventArgs) {
        args.message?.let {
            Snackbar.make(coordinator, it.replace("\n", "").trim(),
                    Snackbar.LENGTH_LONG).show()
            return
        }
        args.messageId?.let {
            Snackbar.make(coordinator, mResources.getString(it),
                    Snackbar.LENGTH_LONG).show()
        }
    }

    override fun showRebootSnackbar() {
        Snackbar.make(coordinator, R.string.reboot, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.action_reboot) { presenter.reboot() }
                .show()
    }

    override fun showExportDialog() {
        mRxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe { granted ->
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
                .addTo(mDisposable)
    }

    override fun showImportDialog() {
        mRxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe { granted ->
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
                .addTo(mDisposable)
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

    companion object {
        fun newInstance(): HomeFragment {
            val fragment = HomeFragment()
            val args = Bundle()
            args.putLong(ARG_CHAIN_ID, Chain.DEFAULT_ID)
            fragment.arguments = args
            return fragment
        }

        private fun flatten(set: String) = set.replace("\\[|]".toRegex(), "")
        private fun toArray(set: String) =
                set.split(",")
                        .map { it.trim() }
                        .toTypedArray()

        const val ARG_CHAIN_ID = "ARG_CHAIN_ID"
    }
}