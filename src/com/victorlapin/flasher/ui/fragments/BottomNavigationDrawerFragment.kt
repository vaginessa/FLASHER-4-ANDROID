package com.victorlapin.flasher.ui.fragments

import android.os.Bundle
import android.view.View
import com.victorlapin.flasher.R
import kotlinx.android.synthetic.main.fragment_navigation.*

class BottomNavigationDrawerFragment : RoundedBottomSheetDialogFragment() {
    override val layoutRes = R.layout.fragment_navigation

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navigation_view.menu.findItem(arguments!!.getInt(KEY_SELECTED_ID)).isChecked = true
    }

    companion object {
        fun newInstance(selectedId: Int): BottomNavigationDrawerFragment {
            val fragment = BottomNavigationDrawerFragment()
            val args = Bundle()
            args.putInt(KEY_SELECTED_ID, selectedId)
            fragment.arguments = args
            return fragment
        }

        private const val KEY_SELECTED_ID = "selected_id"
    }
}