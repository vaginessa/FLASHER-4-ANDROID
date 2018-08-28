package com.victorlapin.flasher.ui.fragments

import android.app.TimePickerDialog
import android.os.Bundle
import android.text.InputType
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.victorlapin.flasher.R
import com.victorlapin.flasher.Screens
import com.victorlapin.flasher.manager.ResourcesManager
import com.victorlapin.flasher.manager.SettingsManager
import com.victorlapin.flasher.model.database.entity.Chain
import com.victorlapin.flasher.presenter.BaseHomeFragmentPresenter
import com.victorlapin.flasher.presenter.ScheduleHomePresenter
import kotlinx.android.synthetic.main.include_schedule_settings.*
import kotlinx.android.synthetic.main.include_toolbar.*
import org.koin.android.ext.android.inject
import org.koin.android.ext.android.release
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class ScheduleFragment : HomeFragment() {
    override val layoutRes = R.layout.fragment_schedule

    private val mSchedulePresenter by inject<ScheduleHomePresenter>()

    override fun providePresenter(): BaseHomeFragmentPresenter = mSchedulePresenter

    private val mSettings by inject<SettingsManager>()
    private val mResources by inject<ResourcesManager>()
    private val mDateTimeFormatter = SimpleDateFormat
            .getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT)
    private val mTimeFormatter = SimpleDateFormat.getTimeInstance(DateFormat.SHORT)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        toolbar.setTitle(R.string.action_schedule)
    }

    override fun onResume() {
        super.onResume()

        val lastRun = mSettings.scheduleLastRun
        lbl_last_run.text = mResources.getString(R.string.schedule_last_run)
                .format(if (lastRun > 0) mDateTimeFormatter.format(Date(lastRun)) else
                    mResources.getString(R.string.schedule_interval_never).toLowerCase())

        val time = mSettings.scheduleTime
        if (time > 0) {
            lbl_time.text = mTimeFormatter.format(Date(time))
        } else {
            lbl_time.text = mResources.getString(R.string.command_tap_to_select)
        }
        lbl_time.setOnClickListener { (presenter as ScheduleHomePresenter).selectTime() }

        val interval = mSettings.scheduleInterval
        lbl_interval.text = if (interval == 0) mResources.getString(R.string.schedule_interval_never) else
            mResources.getQuantityString(R.plurals.schedule_interval, interval, interval)
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

        chk_charging.isChecked = mSettings.scheduleOnlyCharging
        chk_charging.setOnCheckedChangeListener { _, isChecked ->
            (presenter as ScheduleHomePresenter).onOnlyChargingChanged(isChecked)
        }

        chk_idle.isChecked = mSettings.scheduleOnlyIdle
        chk_idle.setOnCheckedChangeListener { _, isChecked ->
            (presenter as ScheduleHomePresenter).onOnlyIdleChanged(isChecked)
        }

        chk_battery.isChecked = mSettings.scheduleOnlyHighBattery
        chk_battery.setOnCheckedChangeListener { _, isChecked ->
            (presenter as ScheduleHomePresenter).onOnlyHighBatteryChanged(isChecked)
        }

        updateNextRun()
    }

    override fun onStop() {
        super.onStop()
        release(Screens.FRAGMENT_SCHEDULE)
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
                android.text.format.DateFormat.is24HourFormat(context))
                .show()
    }

    override fun showSelectIntervalDialog(defInterval: Int) {
        MaterialDialog(context!!)
                .title(res = R.string.schedule_interval_title)
                .input(inputType = InputType.TYPE_CLASS_NUMBER,
                        prefill = defInterval.toString()) { _, input ->
                    val interval = if (input.isBlank()) 0 else input.toString().toInt()
                    (presenter as ScheduleHomePresenter).onIntervalSelected(interval)
                    lbl_interval.text = if (interval == 0)
                        mResources.getString(R.string.schedule_interval_never) else
                        mResources.getQuantityString(R.plurals.schedule_interval, interval, interval)
                    updateNextRun()
                }
                .positiveButton(res = android.R.string.ok)
                .negativeButton(res = android.R.string.cancel)
                .show()
    }

    private fun updateNextRun() = (presenter as ScheduleHomePresenter).updateNextRun()

    override fun showNextRun(hasNext: Boolean, nextRun: Long) {
        val displayText = when {
            (!chk_enable.isChecked) ||
                    (!hasNext) -> mResources.getString(R.string.schedule_interval_never).toLowerCase()
            else -> mDateTimeFormatter.format(Date(nextRun))
        }
        lbl_next_run.text = mResources.getString(R.string.schedule_next_run).format(displayText)
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