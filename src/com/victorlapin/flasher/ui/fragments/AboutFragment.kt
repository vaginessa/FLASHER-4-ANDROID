package com.victorlapin.flasher.ui.fragments

import android.content.res.Configuration
import android.os.Bundle
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.victorlapin.flasher.R
import com.victorlapin.flasher.model.repository.AboutRepository
import com.victorlapin.flasher.presenter.AboutFragmentPresenter
import com.victorlapin.flasher.ui.adapters.AboutAdapter
import com.victorlapin.flasher.view.AboutFragmentView
import kotlinx.android.synthetic.main.fragment_list.*
import kotlinx.android.synthetic.main.include_toolbar.*
import org.koin.android.ext.android.get
import org.koin.androidx.scope.currentScope

class AboutFragment : BaseFragment(), AboutFragmentView {
    override val layoutRes = R.layout.fragment_list

    @InjectPresenter
    lateinit var presenter: AboutFragmentPresenter

    @ProvidePresenter
    fun providePresenter() = currentScope.get<AboutFragmentPresenter>()

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
            when (activity!!.resources.configuration.orientation) {
                Configuration.ORIENTATION_LANDSCAPE -> {
                    val lm = GridLayoutManager(context, GRID_COLUMN_COUNT)
                    lm.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                        override fun getSpanSize(position: Int): Int {
                            return when (adapter!!.getItemViewType(position)) {
                                AboutAdapter.ITEM_TYPE_ITEM -> 1
                                else -> GRID_COLUMN_COUNT
                            }
                        }
                    }
                    layoutManager = lm
                }
                else -> layoutManager = LinearLayoutManager(context)
            }
            itemAnimator = DefaultItemAnimator()
            setHasFixedSize(true)
            adapter = aboutAdapter
        }
    }

    override fun setData(data: List<AboutRepository.ListItem>) {
        list.post { (list.adapter as AboutAdapter).setData(data) }
    }

    companion object {
        private const val GRID_COLUMN_COUNT = 2

        fun newInstance() = AboutFragment()
    }
}