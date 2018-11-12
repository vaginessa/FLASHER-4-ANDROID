package com.victorlapin.flasher.model.repository

import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.victorlapin.flasher.R
import com.victorlapin.flasher.manager.ResourcesManager
import com.victorlapin.flasher.manager.ServicesManager

class AboutRepository(
        private val mServices: ServicesManager,
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
        result.add(getAppInfo(mServices.selfPackageName))

        // show developers
        result.add(ListItem(name = ITEM_TEAM))
        result.add(getCreditItem(nameRes = R.string.about_team_victor,
                descriptionRes = R.string.about_developer))

        // show links
        result.add(ListItem(name = ITEM_LINKS))
        result.add(getLinkItem(nameRes = R.string.about_links_source_code,
                descriptionRes = R.string.about_links_gitlab,
                imageRes = R.drawable.git,
                url = "https://gitlab.com/victorlapin/flasher"))
        result.add(getLinkItem(nameRes = R.string.about_links_donate,
                descriptionRes = R.string.about_links_paypal,
                imageRes = R.drawable.paypal,
                url = "https://www.paypal.me/VictorLapin"))

        // show credits
        result.add(ListItem(name = ITEM_CREDITS))
        result.add(getCreditItem(nameRes = R.string.about_credits_bauke,
                descriptionRes = R.string.about_credits_bauke_text))
        result.add(getCreditItem(nameRes = R.string.about_credits_dave,
                descriptionRes = R.string.about_credits_dave_text))
        result.add(getCreditItem(nameRes = R.string.about_credits_roger,
                descriptionRes = R.string.about_credits_roger_text))
        result.add(getCreditItem(nameRes = R.string.about_credits_bill,
                descriptionRes = R.string.about_credits_bill_text))
        result.add(getCreditItem(nameRes = R.string.about_credits_anton,
                descriptionRes = R.string.about_credits_anton_text))
        result.add(getCreditItem(nameRes = R.string.about_credits_george,
                descriptionRes = R.string.about_credits_anton_text))
        result.add(getCreditItem(nameRes = R.string.about_credits_austin,
                descriptionRes = R.string.about_credits_austin_text))
        result.add(getCreditItem(nameRes = R.string.about_credits_doug,
                descriptionRes = R.string.about_credits_austin_text))
        result.add(getCreditItem(nameRes = R.string.about_credits_jared,
                descriptionRes = R.string.about_credits_jared_text))
        result.add(getCreditItem(nameRes = R.string.about_credits_topjohnwu,
                descriptionRes = R.string.about_credits_topjohnwu_text))
        result.add(getCreditItem(nameRes = R.string.about_credits_aidan,
                descriptionRes = R.string.about_credits_aidan_text))

        return result
    }

    private fun getAppInfo(packageName: String): ListItem {
        val item = ListItem("")
        try {
            val pm = mServices.packageManager
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

    private fun getLinkItem(@StringRes nameRes: Int,
                            @StringRes descriptionRes: Int,
                            @DrawableRes imageRes: Int,
                            url: String): ListItem =
            ListItem(name = mResources.getString(nameRes),
                    description = mResources.getString(descriptionRes),
                    image = mResources.getDrawable(imageRes),
                    url = url)

    private fun getCreditItem(@StringRes nameRes: Int,
                              @StringRes descriptionRes: Int): ListItem {
        val item = ListItem(name = mResources.getString(nameRes),
                description = mResources.getString(descriptionRes))
        item.image = mBuilder.build(item.name.filter { it.isUpperCase() }.toString(),
                mColorGenerator.getColor(item.name))
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