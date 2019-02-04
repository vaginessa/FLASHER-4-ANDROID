package com.victorlapin.flasher.manager

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import androidx.annotation.ArrayRes
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat

class ResourcesManager(private val mContext: Context) {
    fun getString(@StringRes id: Int): String = mContext.getString(id)

    fun getQuantityString(@PluralsRes id: Int, quantity: Int, formatArg: Int): String =
        mContext.resources.getQuantityString(id, quantity, formatArg)

    fun getDrawable(@DrawableRes id: Int): Drawable? = mContext.getDrawable(id)

    fun getColor(@ColorRes id: Int): Int =
        ContextCompat.getColor(mContext, id)

    fun getStringArray(@ArrayRes id: Int): Array<String> =
        mContext.resources.getStringArray(id)

    fun getStringList(@ArrayRes id: Int): List<String> =
        getStringArray(id).toList()

    val resources: Resources
        get() = mContext.resources
}