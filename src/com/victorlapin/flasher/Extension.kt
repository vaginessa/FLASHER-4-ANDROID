package com.victorlapin.flasher

import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View =
        LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)

fun Disposable.addTo(compositeDisposable: CompositeDisposable) =
        compositeDisposable.add(this)

fun View.visible(value: Boolean) {
    this.visibility = if (value) View.VISIBLE else View.GONE
}

fun String.toArrayLowerCase() =
        this.split(",")
                .map { it.trim().toLowerCase() }
                .toTypedArray()

fun String.toArray() =
        this.split(",")
                .map { it.trim() }
                .toTypedArray()

fun String.flatten() = this.replace("\\[|]".toRegex(), "")