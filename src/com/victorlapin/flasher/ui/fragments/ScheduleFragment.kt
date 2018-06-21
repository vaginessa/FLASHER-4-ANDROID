package com.victorlapin.flasher.ui.fragments

import android.os.Bundle
import com.victorlapin.flasher.R
import com.victorlapin.flasher.Screens
import com.victorlapin.flasher.model.database.entity.Chain
import com.victorlapin.flasher.presenter.HomeFragmentPresenter
import com.victorlapin.flasher.presenter.ScheduleHomePresenter
import org.koin.android.ext.android.inject
import org.koin.android.ext.android.releaseContext

class ScheduleFragment : HomeFragment() {
    override val layoutRes = R.layout.fragment_schedule

    private val mSchedulePresenter by inject<ScheduleHomePresenter>()

    override fun providePresenter(): HomeFragmentPresenter = mSchedulePresenter

    override fun onStop() {
        super.onStop()
        releaseContext(Screens.FRAGMENT_SCHEDULE)
    }

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