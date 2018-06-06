package com.victorlapin.flasher.ui.adapters

import android.support.v4.app.FragmentActivity
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.folderselector.FileChooserDialog
import com.victorlapin.flasher.R
import com.victorlapin.flasher.inflate
import com.victorlapin.flasher.model.database.entity.Command
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.item_command.view.*
import java.io.File

class HomeAdapter constructor(
        activity: FragmentActivity
) : RecyclerView.Adapter<HomeAdapter.ViewHolder>() {
    private val mFm = activity.supportFragmentManager
    private val mItems: ArrayList<Command> = arrayListOf()
    private val mCommands = activity.resources.getStringArray(R.array.commands)
    private val mDefaultArgText = activity.resources.getString(R.string.command_tap_to_select)
    private val mWipePartitions = activity.resources.getStringArray(R.array.wipe_partitions).toList()
    private val mBackupPartitions = activity.resources.getStringArray(R.array.backup_partitions).toList()

    // events stuff
    private val mUpdateSubject = PublishSubject.create<Command>()
    val updateEvent: PublishSubject<Command> = mUpdateSubject

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val channel = mItems[position]
        holder.bind(channel)
    }

    override fun onViewRecycled(holder: ViewHolder) {
        holder.unbind()
        super.onViewRecycled(holder)
    }

    override fun getItemCount(): Int = mItems.size

    override fun getItemId(position: Int): Long = mItems[position].id!!

    inner class ViewHolder(parent: ViewGroup) :
            RecyclerView.ViewHolder(parent.inflate(R.layout.item_command)) {

        fun bind(command: Command) {
            when (command.type) {
                Command.TYPE_WIPE -> itemView.image.setImageResource(R.drawable.cellphone_erase)
                Command.TYPE_BACKUP -> itemView.image.setImageResource(R.drawable.backup_restore)
                Command.TYPE_FLASH -> itemView.image.setImageResource(R.drawable.flash)
            }

            val adapter = ArrayAdapter<String>(itemView.context,
                    R.layout.item_spinner,
                    mCommands)
            adapter.setDropDownViewResource(R.layout.item_spinner_dropdown)
            itemView.spinner_type.adapter = adapter
            itemView.spinner_type.setSelection(command.type)
            itemView.spinner_type.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onNothingSelected(parent: AdapterView<*>?) {}
                        override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                            if (command.type != position) {
                                command.type = position
                                command.arg1 = null
                                command.arg2 = null
                                mUpdateSubject.onNext(command)
                            }
                        }
                    }

            itemView.lbl_arg1.text = if (command.arg1 != null) command.arg1 else mDefaultArgText
            itemView.lbl_arg1.setOnClickListener {
                when (command.type) {
                    Command.TYPE_WIPE -> {
                        val indices = if (command.arg1 != null) {
                            val preselected = toArray(command.arg1!!)
                            val i = arrayListOf<Int>()
                            preselected.forEach { i.add(mWipePartitions.indexOf(it)) }
                            i.toTypedArray()
                        } else null
                        MaterialDialog.Builder(itemView.context)
                                .title(itemView.spinner_type.selectedItem.toString())
                                .items(mWipePartitions)
                                .itemsCallbackMultiChoice(indices, { _, _, items ->
                                    command.arg1 = flatten(items.toSet().toString())
                                    mUpdateSubject.onNext(command)
                                    return@itemsCallbackMultiChoice true
                                })
                                .positiveText(android.R.string.ok)
                                .negativeText(android.R.string.cancel)
                                .show()
                    }

                    Command.TYPE_BACKUP -> {
                        val indices = if (command.arg1 != null) {
                            val preselected = toArray(command.arg1!!)
                            val i = arrayListOf<Int>()
                            preselected.forEach { i.add(mBackupPartitions.indexOf(it)) }
                            i.toTypedArray()
                        } else null
                        MaterialDialog.Builder(itemView.context)
                                .title(itemView.spinner_type.selectedItem.toString())
                                .items(mBackupPartitions)
                                .itemsCallbackMultiChoice(indices, { _, _, items ->
                                    command.arg1 = flatten(items.toSet().toString())
                                    mUpdateSubject.onNext(command)
                                    return@itemsCallbackMultiChoice true
                                })
                                .positiveText(android.R.string.ok)
                                .negativeText(android.R.string.cancel)
                                .show()
                    }

                    Command.TYPE_FLASH -> {
                        FileChooserDialog.Builder(itemView.context)
                                .initialPath(command.arg2)
                                .extensionsFilter(".zip")
                                .callback(object : FileChooserDialog.FileCallback {
                                    override fun onFileChooserDismissed(dialog: FileChooserDialog) { }
                                    override fun onFileSelection(dialog: FileChooserDialog, file: File) {
                                        command.arg1 = file.absolutePath
                                        command.arg2 = file.parent
                                        mUpdateSubject.onNext(command)
                                    }
                                })
                                .show(mFm)
                    }
                }
            }
        }

        fun unbind() {
            itemView.spinner_type.onItemSelectedListener = null
            itemView.lbl_arg1.setOnClickListener(null)
        }
    }

    fun setData(words: List<Command>) {
        notifyItemRangeRemoved(0, mItems.size)
        mItems.clear()
        mItems.addAll(words)
        notifyItemRangeInserted(0, mItems.size)
    }

    companion object {
        private fun flatten(set: String) = set.replace("\\[|]".toRegex(), "")
        private fun toArray(set: String) =
                set.split(",")
                        .map { it.trim() }
                        .toTypedArray()
    }
}