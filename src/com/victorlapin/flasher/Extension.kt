package com.victorlapin.flasher

import android.content.Context
import android.support.annotation.LayoutRes
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlin.math.roundToInt

fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View =
        LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)

fun Disposable.addTo(compositeDisposable: CompositeDisposable) =
        compositeDisposable.add(this)

fun View.visible(value: Boolean) {
    this.visibility = if (value) View.VISIBLE else View.GONE
}

fun Context.dpToPixels(dipValue: Float) =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, resources.displayMetrics)
                .roundToInt()

fun Snackbar.adjustLayout(): Snackbar {
    val view = this.view as Snackbar.SnackbarLayout
    view.elevation = context.dpToPixels(2f).toFloat()
    val params = view.layoutParams as CoordinatorLayout.LayoutParams
    params.bottomMargin += context.dpToPixels(92f) // 56dp bottom bar + 36dp
    params.leftMargin += context.dpToPixels(8f)
    params.rightMargin += context.dpToPixels(8f)
    view.layoutParams = params
    return this
}