package com.victorlapin.flasher.model

import java.util.*

class DateBuilder constructor() {
    private val mCalendar = Calendar.getInstance()
    private var mInterval = 0

    constructor(defaultDate: Long) : this() {
        mCalendar.timeInMillis = defaultDate
    }

    constructor(hourOfDay: Int, minute: Int) : this() {
        mCalendar.timeInMillis = System.currentTimeMillis()
        this.hourOfDay = hourOfDay
        this.minute = minute
        if (mCalendar.timeInMillis <= System.currentTimeMillis()) {
            mCalendar.add(Calendar.DAY_OF_YEAR, 1)
        }
    }

    val nextAlarmTime: Long
        get() {
            if (interval > 0) {
                while (mCalendar.timeInMillis < System.currentTimeMillis()) {
                    mCalendar.add(Calendar.DAY_OF_YEAR, interval)
                }
            }
            return mCalendar.timeInMillis
        }

    var hourOfDay
        get() = mCalendar.get(Calendar.HOUR_OF_DAY)
        set(hour) = mCalendar.set(Calendar.HOUR_OF_DAY, hour)

    var minute
        get() = mCalendar.get(Calendar.MINUTE)
        set(minute) = mCalendar.set(Calendar.MINUTE, minute)

    var interval
        get() = mInterval
        set(interval) {
            mInterval = interval
        }

    fun hasNextAlarm() = nextAlarmTime > System.currentTimeMillis()
}