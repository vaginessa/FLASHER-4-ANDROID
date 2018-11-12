package com.victorlapin.flasher.ui.adapters

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.victorlapin.flasher.AboutExternalScreen
import com.victorlapin.flasher.R
import com.victorlapin.flasher.inflate
import com.victorlapin.flasher.manager.ResourcesManager
import com.victorlapin.flasher.model.AboutClickEventArgs
import com.victorlapin.flasher.model.repository.AboutRepository
import kotlinx.android.synthetic.main.item_about.view.*

class AboutAdapter(
        resources: ResourcesManager,
        private val itemClickListener: (AboutClickEventArgs) -> Unit
) : RecyclerView.Adapter<AboutAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val mItems = ArrayList<AboutRepository.ListItem>()

    private val strVersions = resources.getString(R.string.about_versions)
    private val strTeam = resources.getString(R.string.about_the_team)
    private val strCredits = resources.getString(R.string.about_credits)
    private val strLinks = resources.getString(R.string.about_links)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = when (viewType) {
            ITEM_TYPE_VERSIONS, ITEM_TYPE_TEAM, ITEM_TYPE_CREDITS, ITEM_TYPE_LINKS ->
                parent.inflate(R.layout.item_about_category)
            else -> parent.inflate(R.layout.item_about)
        }
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val viewType = getItemViewType(position)
        when (viewType) {
            ITEM_TYPE_VERSIONS -> holder.itemView.lbl_name.text = strVersions
            ITEM_TYPE_TEAM -> holder.itemView.lbl_name.text = strTeam
            ITEM_TYPE_CREDITS -> holder.itemView.lbl_name.text = strCredits
            ITEM_TYPE_LINKS -> holder.itemView.lbl_name.text = strLinks
            ITEM_TYPE_ITEM -> {
                val item = mItems[position]
                holder.itemView.click_container.setOnClickListener(null)
                holder.itemView.lbl_name.text = item.name
                holder.itemView.lbl_description.text = item.description
                holder.itemView.img_picture.setImageDrawable(item.image)
                if (item.url != null) {
                    holder.itemView.click_container.setOnClickListener {
                        itemClickListener(AboutClickEventArgs(
                                AboutExternalScreen(item.url!!.toString())))
                    }

                } else {
                    holder.itemView.click_container.setOnClickListener(null)
                }
            }
        }
    }

    override fun getItemCount() = mItems.size

    override fun getItemViewType(position: Int): Int {
        val item = mItems[position]
        return when (item.name) {
            AboutRepository.ITEM_TEAM -> ITEM_TYPE_TEAM
            AboutRepository.ITEM_VERSIONS -> ITEM_TYPE_VERSIONS
            AboutRepository.ITEM_CREDITS -> ITEM_TYPE_CREDITS
            AboutRepository.ITEM_LINKS -> ITEM_TYPE_LINKS
            else -> ITEM_TYPE_ITEM
        }
    }

    fun setData(data: List<AboutRepository.ListItem>) {
        mItems.clear()
        mItems.addAll(data)
        notifyDataSetChanged()
    }

    companion object {
        private const val ITEM_TYPE_ITEM = 0
        private const val ITEM_TYPE_VERSIONS = 1
        private const val ITEM_TYPE_TEAM = 2
        private const val ITEM_TYPE_CREDITS = 3
        private const val ITEM_TYPE_LINKS = 4
    }
}