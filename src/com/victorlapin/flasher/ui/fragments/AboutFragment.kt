package com.victorlapin.flasher.ui.fragments

import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.victorlapin.flasher.Const
import com.victorlapin.flasher.R
import com.victorlapin.flasher.model.repository.AboutRepository
import com.victorlapin.flasher.presenter.AboutFragmentPresenter
import com.victorlapin.flasher.ui.adapters.AboutAdapter
import com.victorlapin.flasher.view.AboutFragmentView
import kotlinx.android.synthetic.main.fragment_list.*
import kotlinx.android.synthetic.main.include_toolbar.*
import org.koin.android.ext.android.get

class AboutFragment : BaseFragment(), AboutFragmentView {
    override val layoutRes = R.layout.fragment_list
    override val scopeName = Const.FRAGMENT_ABOUT

    @InjectPresenter
    lateinit var presenter: AboutFragmentPresenter

    @ProvidePresenter
    fun providePresenter() = get<AboutFragmentPresenter>()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        toolbar.setTitle(R.string.action_about)
        toolbar.setNavigationIcon(R.drawable.close)
        toolbar.setNavigationOnClickListener { presenter.onBackPressed() }

        val aboutAdapter = AboutAdapter(
                resources = get(),
                itemClickListener = { presenter.onItemClick(it) }
        )
        list.apply {
            layoutManager = LinearLayoutManager(context)
            itemAnimator = DefaultItemAnimator()
            setHasFixedSize(true)
            adapter = aboutAdapter
        }
    }

    override fun setData(data: List<AboutRepository.ListItem>) {
        list.post { (list.adapter as AboutAdapter).setData(data) }
    }

    companion object {
        fun newInstance() = AboutFragment()
    }
}