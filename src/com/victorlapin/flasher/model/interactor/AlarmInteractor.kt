package com.victorlapin.flasher.model.interactor

import com.victorlapin.flasher.model.repository.AlarmRepository
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class AlarmInteractor(private val mRepo: AlarmRepository) {
    fun setAlarm(): Single<Any> = mRepo.setAlarm()
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())

    fun cancelAlarm(): Single<Any> = mRepo.cancelAlarm()
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
}