package com.victorlapin.flasher.ui

import android.content.Context
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceViewHolder
import android.util.AttributeSet
import com.victorlapin.flasher.R
import kotlinx.android.synthetic.main.include_toolbar.view.*
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import ru.terrakok.cicerone.Router

class ToolbarPreference @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet,
        defStyleAttr: Int = 0
) : Preference(context, attrs, defStyleAttr), KoinComponent {
    private val mRouter by inject<Router>()

    init {
        layoutResource = R.layout.include_toolbar
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        holder.itemView.toolbar.setTitle(R.string.action_settings)
        holder.itemView.toolbar.setNavigationIcon(R.drawable.close)
        holder.itemView.toolbar.setNavigationOnClickListener { mRouter.exit() }
        holder.isDividerAllowedBelow = false
    }


}
