package com.victorlapin.flasher.manager

import android.content.ContentResolver
import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.support.annotation.ArrayRes
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import android.support.v4.content.ContextCompat

class ResourcesManager(private val mContext: Context) {
    fun getString(@StringRes id: Int): String = mContext.getString(id)

    fun getDrawable(@DrawableRes id: Int): Drawable = mContext.getDrawable(id)

    val resolver: ContentResolver
        get() = mContext.contentResolver

    fun getColor(@ColorRes id: Int): Int =
            ContextCompat.getColor(mContext, id)

    fun getStringSet(@ArrayRes id: Int): Set<String> =
            mContext.resources.getStringArray(id).toSet()

    val resources: Resources
        get() = mContext.resources
}