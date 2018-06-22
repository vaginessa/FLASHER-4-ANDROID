package com.victorlapin.flasher.ui.fragments

import android.app.TimePickerDialog
import android.os.Bundle
import android.text.InputType
import com.afollestad.materialdialogs.MaterialDialog
import com.victorlapin.flasher.R
import com.victorlapin.flasher.Screens
import com.victorlapin.flasher.manager.SettingsManager
import com.victorlapin.flasher.model.database.entity.Chain
import com.victorlapin.flasher.presenter.HomeFragmentPresenter
import com.victorlapin.flasher.presenter.ScheduleHomePresenter
import kotlinx.android.synthetic.main.include_schedule_settings.*
import org.koin.android.ext.android.inject
import org.koin.android.ext.android.releaseContext
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class ScheduleFragment : HomeFragment() {
    override val layoutRes = R.layout.fragment_schedule

    private val mSchedulePresenter by inject<ScheduleHomePresenter>()

    override fun providePresenter(): HomeFragmentPresenter = mSchedulePresenter

    private val mSettings by inject<SettingsManager>()
    private val mTimeFormatter = SimpleDateFormat.getTimeInstance(DateFormat.SHORT)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        chk_enable.isChecked = mSettings.useSchedule
        chk_enable.setOnCheckedChangeListener { _, isChecked ->
            (presenter as ScheduleHomePresenter).onScheduleEnabledChange(isChecked)
        }

        val time = mSettings.scheduleTime
        if (time > 0) {
            lbl_time.text = mTimeFormatter.format(Date(time))
        }
        lbl_time.setOnClickListener { (presenter as ScheduleHomePresenter).selectTime() }

        val period = mSettings.schedulePeriod
        lbl_period.text = when (period) {
            0 -> getString(R.string.schedule_period_never)
            1 -> getString(R.string.schedule_period_daily)
            7 -> getString(R.string.schedule_period_weekly)
            else -> resources.getQuantityString(R.plurals.schedule_period, period, period)
        }
        lbl_period.setOnClickListener { (presenter as ScheduleHomePresenter).selectPeriod() }
    }

    override fun onStop() {
        super.onStop()
        releaseContext(Screens.FRAGMENT_SCHEDULE)
    }

    override fun showSelectTimeDialog(defHourOfDay: Int, defMinute: Int) {
        val callback = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            (presenter as ScheduleHomePresenter).onTimeSelected(hourOfDay, minute)
            lbl_time.text = mTimeFormatter.format(Date(mSettings.scheduleTime))
        }
        TimePickerDialog(context!!,
                callback,
                defHourOfDay,
                defMinute,
                true)
                .show()
    }

    override fun showSelectPeriodDialog(defPeriod: Int) {
        MaterialDialog.Builder(context!!)
                .title(R.string.schedule_period_title)
                .inputType(InputType.TYPE_CLASS_NUMBER)
                .input(null, defPeriod.toString(), true) { _, input ->
                    val period = if (input.isBlank()) 0 else input.toString().toInt()
                    (presenter as ScheduleHomePresenter).onPeriodSelected(period)
                    lbl_period.text = when (period) {
                        0 -> getString(R.string.schedule_period_never)
                        1 -> getString(R.string.schedule_period_daily)
                        7 -> getString(R.string.schedule_period_weekly)
                        else -> resources.getQuantityString(R.plurals.schedule_period, period, period)
                    }
                }
                .negativeText(android.R.string.cancel)
                .show()
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