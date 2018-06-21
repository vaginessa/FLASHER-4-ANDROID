package com.victorlapin.flasher.ui.fragments

import android.os.Bundle
import com.victorlapin.flasher.model.database.entity.Chain

class ScheduleFragment : HomeFragment() {
    companion object {
        fun newInstance(): ScheduleFragment {
            val fragment = ScheduleFragment()
            val args = Bundle()
            args.putLong(ARG_CHAIN_ID, Chain.SCHEDULE_ID)
            fragment.arguments = args
            return fragment
        }
    }
}