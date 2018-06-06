package com.victorlapin.flasher.view

import com.arellomobile.mvp.MvpView
import com.victorlapin.flasher.model.database.entity.Command

interface HomeFragmentView : MvpView {
    fun setData(commands: List<Command>)
}