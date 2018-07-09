package com.victorlapin.flasher.ui.adapters

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.victorlapin.flasher.R
import com.victorlapin.flasher.inflate
import com.victorlapin.flasher.manager.ResourcesManager
import com.victorlapin.flasher.model.CommandClickEventArgs
import com.victorlapin.flasher.model.database.entity.Command
import com.victorlapin.flasher.visible
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.item_command.view.*
import java.util.*

class HomeAdapter constructor(
        resources: ResourcesManager
) : RecyclerView.Adapter<HomeAdapter.ViewHolder>() {
    private val mItems: ArrayList<Command> = arrayListOf()
    private val mCommands = resources.getStringArray(R.array.commands)
    private val mDefaultArgText = resources.getString(R.string.command_tap_to_select)
    private val mEnterMaskText = resources.getString(R.string.command_tap_to_enter_mask)
    private val mSelectFolderText = resources.getString(R.string.command_tap_to_select_folder)

    // events stuff
    private val mChangeTypeSubject = PublishSubject.create<Pair<Command, Int>>()
    val changeTypeEvent: PublishSubject<Pair<Command, Int>> = mChangeTypeSubject
    private val mArgsClickSubject = PublishSubject.create<CommandClickEventArgs>()
    val argsClickEvent: PublishSubject<CommandClickEventArgs> = mArgsClickSubject

    fun getItems(): ArrayList<Command> {
        val result = arrayListOf<Command>()
        mItems.forEach { result.add(it.clone()) }
        return result
    }

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
                Command.TYPE_FLASH_FILE -> itemView.image.setImageResource(R.drawable.flash)
                Command.TYPE_FLASH_MASK -> itemView.image.setImageResource(R.drawable.regex)
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
                            val pair = Pair(command.clone(), position)
                            mChangeTypeSubject.onNext(pair)
                        }
                    }

            if (command.type == Command.TYPE_FLASH_MASK) {
                itemView.lbl_arg2.visible(true)
                itemView.lbl_arg1.text = if (command.arg1 != null) command.arg1 else mEnterMaskText
                itemView.lbl_arg2.text = if (command.arg2 != null) command.arg2 else mSelectFolderText
            } else {
                itemView.lbl_arg2.visible(false)
                itemView.lbl_arg1.text = if (command.arg1 != null) command.arg1 else mDefaultArgText
            }
            itemView.lbl_arg1.setOnClickListener {
                mArgsClickSubject.onNext(CommandClickEventArgs(command.clone(),
                        CommandClickEventArgs.ARG1))
            }
            itemView.lbl_arg2.setOnClickListener {
                mArgsClickSubject.onNext(CommandClickEventArgs(command.clone(),
                        CommandClickEventArgs.ARG2))
            }
        }

        fun unbind() {
            itemView.spinner_type.onItemSelectedListener = null
            itemView.lbl_arg1.setOnClickListener(null)
            itemView.lbl_arg2.setOnClickListener(null)
        }
    }

    fun setData(commands: List<Command>) {
        val callback = HomeAdapterDiffUtilCallback(mItems, commands)
        val diffResult = DiffUtil.calculateDiff(callback)
        mItems.clear()
        mItems.addAll(commands)
        diffResult.dispatchUpdatesTo(this)
    }

    fun moveItems(fromPosition: Int, toPosition: Int) {
        val buffer = getItems()
        Collections.swap(buffer, fromPosition, if (toPosition == -1) 0 else toPosition)
        mItems.clear()
        mItems.addAll(buffer)
        notifyItemMoved(fromPosition, toPosition)
    }

    fun onMoveFinished() {
        var i = 0
        mItems.forEach { it.orderNumber = ++i }
    }
}