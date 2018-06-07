package com.victorlapin.flasher.model.interactor

import com.victorlapin.flasher.model.repository.AboutRepository

class AboutInteractor(private val mRepo: AboutRepository) {
    fun getData() = mRepo.getData()
}