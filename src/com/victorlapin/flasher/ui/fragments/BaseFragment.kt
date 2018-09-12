package com.victorlapin.flasher.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.MvpAppCompatFragment
import org.koin.android.ext.android.getKoin
import org.koin.core.scope.Scope

abstract class BaseFragment : MvpAppCompatFragment() {
    abstract val layoutRes: Int
    abstract val scopeName: String

    private lateinit var mScope: Scope

    override fun onCreate(savedInstanceState: Bundle?) {
        mScope = getKoin().createScope(scopeName)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(layoutRes, container, false)

    override fun onStop() {
        super.onStop()
        mScope.close()
    }
}