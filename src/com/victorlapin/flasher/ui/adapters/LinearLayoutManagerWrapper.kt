package com.victorlapin.flasher.ui.adapters

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView

class LinearLayoutManagerWrapper(context: Context) : LinearLayoutManager(context) {
    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        try {
            super.onLayoutChildren(recycler, state)
        } catch (ex: IndexOutOfBoundsException) {
            ex.printStackTrace()
        }
    }
}