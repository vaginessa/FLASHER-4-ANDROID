package com.victorlapin.flasher.view

import com.arellomobile.mvp.MvpView
import com.victorlapin.flasher.model.repository.AboutRepository

interface AboutFragmentView: MvpView {
    fun setData(data: List<AboutRepository.ListItem>)
}