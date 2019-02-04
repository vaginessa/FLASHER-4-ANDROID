package com.victorlapin.flasher.presenter

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.victorlapin.flasher.model.AboutClickEventArgs
import com.victorlapin.flasher.model.interactor.AboutInteractor
import com.victorlapin.flasher.view.AboutFragmentView
import ru.terrakok.cicerone.Router

@InjectViewState
class AboutFragmentPresenter(
    private val mInteractor: AboutInteractor,
    private val mRouter: Router
) : MvpPresenter<AboutFragmentView>() {
    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        showData()
    }

    private fun showData() {
        val data = mInteractor.getData()
        viewState.setData(data)
    }

    fun onItemClick(args: AboutClickEventArgs) =
        mRouter.navigateTo(args.screen)

    fun onBackPressed() = mRouter.exit()
}