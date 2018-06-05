package com.victorlapin.flasher.ui.adapters

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.victorlapin.flasher.R
import com.victorlapin.flasher.inflate
import com.victorlapin.flasher.model.database.entity.Command
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.item_command.view.*

class HomeAdapter : RecyclerView.Adapter<HomeAdapter.ViewHolder>() {
    private val mItems: ArrayList<Command> = arrayListOf()

    // events stuff
    private val mClickSubject = PublishSubject.create<Command>()
    val clickEvent: PublishSubject<Command> = mClickSubject

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val channel = mItems[position]
        holder.bind(channel)
    }

    override fun getItemCount(): Int = mItems.size

    override fun getItemId(position: Int): Long = mItems[position].id!!

    inner class ViewHolder(parent: ViewGroup) :
            RecyclerView.ViewHolder(parent.inflate(R.layout.item_command)) {

        fun bind(command: Command) {
            itemView.card.setOnClickListener { mClickSubject.onNext(command) }
        }
    }

    fun setData(words: List<Command>) {
        notifyItemRangeRemoved(0, mItems.size)
        mItems.clear()
        mItems.addAll(words)
        notifyItemRangeInserted(0, mItems.size)
    }
}