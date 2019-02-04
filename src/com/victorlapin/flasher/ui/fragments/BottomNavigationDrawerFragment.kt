package com.victorlapin.flasher.ui.fragments

import android.os.Bundle
import android.view.View
import com.victorlapin.flasher.R
import kotlinx.android.synthetic.main.fragment_navigation.*

class BottomNavigationDrawerFragment : RoundedBottomSheetDialogFragment() {
    override val layoutRes = R.layout.fragment_navigation

    private var clickListener: (Int) -> Unit = {}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navigation_view.menu.findItem(arguments!!.getInt(KEY_SELECTED_ID)).isChecked = true
        navigation_view.setNavigationItemSelectedListener {
            clickListener(it.itemId)
            dismiss()
            true
        }
    }

    companion object {
        fun newInstance(
            selectedId: Int,
            clickListener: (Int) -> Unit = {}
        ): BottomNavigationDrawerFragment {
            val fragment = BottomNavigationDrawerFragment()
            val args = Bundle()
            args.putInt(KEY_SELECTED_ID, selectedId)
            fragment.arguments = args
            fragment.clickListener = clickListener
            return fragment
        }

        private const val KEY_SELECTED_ID = "selected_id"
    }
}