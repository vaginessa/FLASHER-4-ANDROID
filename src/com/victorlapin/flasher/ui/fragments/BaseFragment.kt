package com.victorlapin.flasher.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.MvpAppCompatFragment
import org.koin.android.scope.ext.android.bindScope
import org.koin.android.scope.ext.android.createScope

abstract class BaseFragment : MvpAppCompatFragment() {
    abstract val layoutRes: Int
    abstract val scopeName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        bindScope(createScope(scopeName))
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(layoutRes, container, false)
}