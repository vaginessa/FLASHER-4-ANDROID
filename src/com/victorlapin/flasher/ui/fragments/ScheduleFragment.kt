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
    private val mDateTimeFormatter = SimpleDateFormat
            .getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT)
    private val mTimeFormatter = SimpleDateFormat.getTimeInstance(DateFormat.SHORT)

    override fun onResume() {
        super.onResume()

        val lastRun = mSettings.alarmLastRun
        lbl_last_run.text = getString(R.string.alarm_last_run,
                if (lastRun > 0) mDateTimeFormatter.format(Date(lastRun)) else
                    getString(R.string.schedule_interval_never).toLowerCase())

        val time = mSettings.scheduleTime
        if (time > 0) {
            lbl_time.text = mTimeFormatter.format(Date(time))
        } else {
            lbl_time.text = getString(R.string.command_tap_to_select)
        }
        lbl_time.setOnClickListener { (presenter as ScheduleHomePresenter).selectTime() }

        val interval = mSettings.scheduleInterval
        lbl_interval.text = when (interval) {
            0 -> getString(R.string.schedule_interval_never)
            1 -> getString(R.string.schedule_interval_daily)
            7 -> getString(R.string.schedule_interval_weekly)
            else -> resources.getQuantityString(R.plurals.schedule_interval, interval, interval)
        }
        lbl_interval.setOnClickListener { (presenter as ScheduleHomePresenter).selectInterval() }

        if (time > 0) {
            chk_enable.isEnabled = true
            chk_enable.isChecked = mSettings.useSchedule
        } else {
            mSettings.useSchedule = false
            chk_enable.isEnabled = false
            chk_enable.isChecked = false
        }
        chk_enable.setOnCheckedChangeListener { _, isChecked ->
            (presenter as ScheduleHomePresenter).onScheduleEnabledChange(isChecked)
            updateNextRun()
        }

        updateNextRun()
    }

    override fun onStop() {
        super.onStop()
        releaseContext(Screens.FRAGMENT_SCHEDULE)
    }

    override fun showSelectTimeDialog(defHourOfDay: Int, defMinute: Int) {
        val callback = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            (presenter as ScheduleHomePresenter).onTimeSelected(hourOfDay, minute)
            lbl_time.text = mTimeFormatter.format(Date(mSettings.scheduleTime))
            chk_enable.isEnabled = true
            updateNextRun()
        }
        TimePickerDialog(context!!,
                callback,
                defHourOfDay,
                defMinute,
                true)
                .show()
    }

    override fun showSelectIntervalDialog(defInterval: Int) {
        MaterialDialog.Builder(context!!)
                .title(R.string.schedule_interval_title)
                .inputType(InputType.TYPE_CLASS_NUMBER)
                .input(null, defInterval.toString(), true) { _, input ->
                    val interval = if (input.isBlank()) 0 else input.toString().toInt()
                    (presenter as ScheduleHomePresenter).onIntervalSelected(interval)
                    lbl_interval.text = when (interval) {
                        0 -> getString(R.string.schedule_interval_never)
                        1 -> getString(R.string.schedule_interval_daily)
                        7 -> getString(R.string.schedule_interval_weekly)
                        else -> resources.getQuantityString(R.plurals.schedule_interval, interval, interval)
                    }
                    updateNextRun()
                }
                .negativeText(android.R.string.cancel)
                .show()
    }

    private fun updateNextRun() = (presenter as ScheduleHomePresenter).updateNextRun()

    override fun showNextRun(nextRun: Long) {
        val displayText = when {
            (!chk_enable.isChecked) ||
            (nextRun < System.currentTimeMillis()) -> getString(R.string.schedule_interval_never).toLowerCase()
            else -> mDateTimeFormatter.format(Date(nextRun))
        }
        lbl_next_run.text = getString(R.string.alarm_next_run, displayText)
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