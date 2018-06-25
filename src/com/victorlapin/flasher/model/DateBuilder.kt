package com.victorlapin.flasher.model

import java.util.*

class DateBuilder constructor() {
    private val mCalendar = Calendar.getInstance()
    private var mIsDateSet: Boolean = false
    private var mPeriod = 1

    constructor(defaultDate: Long) : this() {
        mCalendar.timeInMillis = defaultDate
        mIsDateSet = true
    }

    constructor(hourOfDay: Int, minute: Int) : this() {
        this.hourOfDay = hourOfDay
        this.minute = minute
    }

    val nextAlarmTime: Long
        get() {
            if (!mIsDateSet) {
                val h = hourOfDay
                val m = minute
                mCalendar.timeInMillis = System.currentTimeMillis()
                hourOfDay = h
                minute = m
            }
            if (mCalendar.timeInMillis < System.currentTimeMillis()) {
                mCalendar.add(Calendar.DAY_OF_YEAR, period)
            }
            return mCalendar.timeInMillis
        }

    var hourOfDay
        get() = mCalendar.get(Calendar.HOUR_OF_DAY)
        set(hour) = mCalendar.set(Calendar.HOUR_OF_DAY, hour)

    var minute
        get() = mCalendar.get(Calendar.MINUTE)
        set(minute) = mCalendar.set(Calendar.MINUTE, minute)

    var period
        get() = mPeriod
        set(period) {
            mPeriod = period
        }
}