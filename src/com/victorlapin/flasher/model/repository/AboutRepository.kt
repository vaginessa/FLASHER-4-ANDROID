package com.victorlapin.flasher.model.repository

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.victorlapin.flasher.R
import com.victorlapin.flasher.manager.ResourcesManager

class AboutRepository(
        private val mContext: Context,
        private val mResources: ResourcesManager
) {
    private val mColorGenerator = ColorGenerator.MATERIAL
    private val mBuilder = TextDrawable.builder()
            .beginConfig()
            .textColor(mResources.getColor(android.R.color.white))
            .fontSize(48)
            .bold()
            .toUpperCase()
            .endConfig()
            .round()

    fun getData(): List<ListItem> {
        val result = ArrayList<ListItem>()

        // show versions
        result.add(ListItem(name = ITEM_VERSIONS))
        // the app itself
        var item: ListItem = getAppInfo(mContext.packageName)
        result.add(item)

        // show developers
        result.add(ListItem(name = ITEM_TEAM))
        item = ListItem(mResources.getString(R.string.about_team_victor))
        item.description = mResources.getString(R.string.about_developer)
        var color = mColorGenerator.getColor(item.name)
        item.image = mBuilder.build("VL", color)
        result.add(item)

        // show links
        result.add(ListItem(name = ITEM_LINKS))
        item = ListItem(mResources.getString(R.string.about_links_source_code))
        item.description = mResources.getString(R.string.about_links_gitlab)
        item.url = "https://gitlab.com/victorlapin/flasher"
        item.image = mResources.getDrawable(R.drawable.git)
        result.add(item)
        item = ListItem(mResources.getString(R.string.about_links_donate))
        item.description = mResources.getString(R.string.about_links_paypal)
        item.url = "https://www.paypal.me/VictorLapin/150"
        item.image = mResources.getDrawable(R.drawable.paypal)
        result.add(item)

        // show credits
        result.add(ListItem(name = ITEM_CREDITS))
        item = ListItem(mResources.getString(R.string.about_credits_bauke))
        item.description = mResources.getString(R.string.about_credits_bauke_text)
        color = mColorGenerator.getColor(item.name)
        item.image = mBuilder.build("BZ", color)
        result.add(item)
        item = ListItem(mResources.getString(R.string.about_credits_dave))
        item.description = mResources.getString(R.string.about_credits_dave_text)
        color = mColorGenerator.getColor(item.name)
        item.image = mBuilder.build("DW", color)
        result.add(item)
        item = ListItem(mResources.getString(R.string.about_credits_roger))
        item.description = mResources.getString(R.string.about_credits_roger_text)
        color = mColorGenerator.getColor(item.name)
        item.image = mBuilder.build("RT", color)
        result.add(item)
        item = ListItem(mResources.getString(R.string.about_credits_bill))
        item.description = mResources.getString(R.string.about_credits_bill_text)
        color = mColorGenerator.getColor(item.name)
        item.image = mBuilder.build("BB", color)
        result.add(item)
        item = ListItem(mResources.getString(R.string.about_credits_anton))
        item.description = mResources.getString(R.string.about_credits_anton_text)
        color = mColorGenerator.getColor(item.name)
        item.image = mBuilder.build("AK", color)
        result.add(item)
        item = ListItem(mResources.getString(R.string.about_credits_george))
        item.description = mResources.getString(R.string.about_credits_anton_text)
        color = mColorGenerator.getColor(item.name)
        item.image = mBuilder.build("GG", color)
        result.add(item)
        item = ListItem(mResources.getString(R.string.about_credits_austin))
        item.description = mResources.getString(R.string.about_credits_austin_text)
        color = mColorGenerator.getColor(item.name)
        item.image = mBuilder.build("AA", color)
        result.add(item)
        item = ListItem(mResources.getString(R.string.about_credits_doug))
        item.description = mResources.getString(R.string.about_credits_austin_text)
        color = mColorGenerator.getColor(item.name)
        item.image = mBuilder.build("DH", color)
        result.add(item)
        item = ListItem(mResources.getString(R.string.about_credits_jared))
        item.description = mResources.getString(R.string.about_credits_jared_text)
        color = mColorGenerator.getColor(item.name)
        item.image = mBuilder.build("JR", color)
        result.add(item)
        item = ListItem(mResources.getString(R.string.about_credits_topjohnwu))
        item.description = mResources.getString(R.string.about_credits_topjohnwu_text)
        color = mColorGenerator.getColor(item.name)
        item.image = mBuilder.build("JW", color)
        result.add(item)
        item = ListItem(mResources.getString(R.string.about_credits_aidan))
        item.description = mResources.getString(R.string.about_credits_aidan_text)
        color = mColorGenerator.getColor(item.name)
        item.image = mBuilder.build("AF", color)
        result.add(item)

        return result
    }

    private fun getAppInfo(packageName: String): ListItem {
        val item = ListItem("")
        try {
            val pm = mContext.packageManager
            val packageInfo = pm.getPackageInfo(packageName, 0)
            item.name = pm.getApplicationLabel(packageInfo.applicationInfo)
            item.image = packageInfo.applicationInfo.loadIcon(pm)
            item.description = packageInfo.versionName
        } catch (ex: PackageManager.NameNotFoundException) {
            ex.printStackTrace()
            item.name = packageName
            item.description = mResources.getString(R.string.not_found).toUpperCase()
            item.isError = true
        }

        return item
    }

    data class ListItem(
            var name: CharSequence,
            var description: CharSequence? = null,
            var image: Drawable? = null,
            var isError: Boolean = false,
            var url : CharSequence? = null
    )

    companion object {
        const val ITEM_VERSIONS = "ITEM_VERSIONS"
        const val ITEM_TEAM = "ITEM_TEAM"
        const val ITEM_CREDITS = "ITEM_CREDITS"
        const val ITEM_LINKS = "ITEM_LINKS"
    }
}