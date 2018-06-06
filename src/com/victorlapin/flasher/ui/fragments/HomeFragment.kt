package com.victorlapin.flasher.ui.fragments

import android.Manifest
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.folderselector.FileChooserDialog
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.tbruyelle.rxpermissions2.RxPermissions
import com.victorlapin.flasher.R
import com.victorlapin.flasher.Screens
import com.victorlapin.flasher.addTo
import com.victorlapin.flasher.model.database.entity.Command
import com.victorlapin.flasher.presenter.HomeFragmentPresenter
import com.victorlapin.flasher.ui.adapters.HomeAdapter
import com.victorlapin.flasher.view.HomeFragmentView
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.include_toolbar.*
import org.koin.android.ext.android.inject
import org.koin.android.ext.android.releaseContext
import java.io.File

class HomeFragment : BaseFragment(), HomeFragmentView {
    override val layoutRes = R.layout.fragment_home

    private val mPresenter by inject<HomeFragmentPresenter>()

    @InjectPresenter
    lateinit var presenter: HomeFragmentPresenter

    @ProvidePresenter
    fun providePresenter() = mPresenter

    private val mEventsDisposable = CompositeDisposable()

    private val mAdapter by inject<HomeAdapter>()

    private val mRxPermissions by lazy {
        RxPermissions(activity!!)
    }

    private lateinit var mWipePartitions: List<String>
    private lateinit var mBackupPartitions: List<String>

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mWipePartitions = resources.getStringArray(R.array.wipe_partitions).toList()
        mBackupPartitions = resources.getStringArray(R.array.backup_partitions).toList()

        setupEvents()
        list.apply {
            layoutManager = LinearLayoutManager(context)
            itemAnimator = DefaultItemAnimator()
            setHasFixedSize(true)
            adapter = mAdapter
        }
        toolbar_title.text = context!!.getString(R.string.title_home)

        val swipeCallback = object : ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.START or ItemTouchHelper.END) {
            override fun isLongPressDragEnabled(): Boolean = false

            override fun isItemViewSwipeEnabled(): Boolean = true

            override fun onMove(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?,
                                target: RecyclerView.ViewHolder?): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val id = (viewHolder as HomeAdapter.ViewHolder).itemId
                presenter.onCommandSwiped(id)
            }
        }
        ItemTouchHelper(swipeCallback).attachToRecyclerView(list)
    }

    override fun onStop() {
        super.onStop()
        releaseContext(Screens.FRAGMENT_HOME)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mEventsDisposable.dispose()
    }

    private fun setupEvents() {
        mAdapter.changeTypeEvent
                .subscribe { presenter.onCommandTypeChanged(it) }
                .addTo(mEventsDisposable)
        mAdapter.argsClickEvent
                .subscribe { presenter.onArgumentsClicked(it) }
                .addTo(mEventsDisposable)
    }

    override fun setData(commands: List<Command>) {
        list.post {
            mAdapter.setData(commands)
            if (commands.isNotEmpty()) {
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
                .itemsCallbackMultiChoice(indices, { _, _, items ->
                    command.arg1 = flatten(items.toSet().toString())
                    presenter.onCommandUpdated(command)
                    return@itemsCallbackMultiChoice true
                })
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
                .itemsCallbackMultiChoice(indices, { _, _, items ->
                    command.arg1 = flatten(items.toSet().toString())
                    presenter.onCommandUpdated(command)
                    return@itemsCallbackMultiChoice true
                })
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel)
                .show()
    }

    override fun showFlashDialog(command: Command, startPath: String?) {
        mRxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE)
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
                        AlertDialog.Builder(context!!)
                                .setTitle(R.string.app_name)
                                .setMessage(R.string.permission_denied_storage)
                                .setCancelable(true)
                                .setPositiveButton(android.R.string.ok, null)
                                .show()
                    }
                }
    }

    companion object {
        fun newInstance() = HomeFragment()

        private fun flatten(set: String) = set.replace("\\[|]".toRegex(), "")
        private fun toArray(set: String) =
                set.split(",")
                        .map { it.trim() }
                        .toTypedArray()
    }
}