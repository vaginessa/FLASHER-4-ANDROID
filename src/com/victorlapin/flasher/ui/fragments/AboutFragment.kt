package com.victorlapin.flasher.ui.fragments

import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.victorlapin.flasher.R
import com.victorlapin.flasher.Screens
import com.victorlapin.flasher.addTo
import com.victorlapin.flasher.model.repository.AboutRepository
import com.victorlapin.flasher.presenter.AboutFragmentPresenter
import com.victorlapin.flasher.ui.adapters.AboutAdapter
import com.victorlapin.flasher.view.AboutFragmentView
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_list.*
import kotlinx.android.synthetic.main.include_toolbar.*
import org.koin.android.ext.android.inject
import org.koin.android.ext.android.release

class AboutFragment : BaseFragment(), AboutFragmentView {
    override val layoutRes = R.layout.fragment_list

    private val mPresenter by inject<AboutFragmentPresenter>()

    @InjectPresenter
    lateinit var presenter: AboutFragmentPresenter

    @ProvidePresenter
    fun providePresenter() = mPresenter

    private val mEventsDisposable = CompositeDisposable()

    private val mAdapter by inject<AboutAdapter>()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        toolbar.setTitle(R.string.action_about)
        toolbar.setNavigationIcon(R.drawable.close)
        toolbar.setNavigationOnClickListener { presenter.onBackPressed() }

        setupEvents()
        list.apply {
            layoutManager = LinearLayoutManager(context)
            itemAnimator = DefaultItemAnimator()
            setHasFixedSize(true)
            adapter = mAdapter
        }
    }

    override fun onStop() {
        super.onStop()
        release(Screens.FRAGMENT_ABOUT)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mEventsDisposable.dispose()
    }

    private fun setupEvents() {
        mAdapter.clickEvent
                .subscribe { presenter.onItemClick(it) }
                .addTo(mEventsDisposable)
    }

    override fun setData(data: List<AboutRepository.ListItem>) {
        list.post { mAdapter.setData(data) }
    }

    companion object {
        fun newInstance() = AboutFragment()
    }
}