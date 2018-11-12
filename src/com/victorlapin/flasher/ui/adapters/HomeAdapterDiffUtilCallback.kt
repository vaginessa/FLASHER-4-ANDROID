package com.victorlapin.flasher.ui.adapters

import androidx.recyclerview.widget.DiffUtil
import com.victorlapin.flasher.model.database.entity.Command

class HomeAdapterDiffUtilCallback(
        private val oldList: List<Command>,
        private val newList: List<Command>
) : DiffUtil.Callback() {
    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return (oldItem.type == newItem.type)
                && (oldItem.arg1 == newItem.arg1)
                && (oldItem.arg2 == newItem.arg2)
    }
}