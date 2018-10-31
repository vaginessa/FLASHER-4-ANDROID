package com.victorlapin.flasher.ui.fragments

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.support.design.widget.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.victorlapin.flasher.R
import com.victorlapin.flasher.Const
import com.victorlapin.flasher.manager.SettingsManager
import io.reactivex.subjects.PublishSubject
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.android.inject
import org.koin.standalone.KoinComponent

abstract class RoundedBottomSheetDialogFragment : BottomSheetDialogFragment(),
        KoinComponent {
    abstract val layoutRes: Int

    private val mScope = getKoin().getOrCreateScope(Const.FRAGMENT_BOTTOM)
    private val mSettings by inject<SettingsManager>()

    protected var dismissListener: () -> Unit = {}

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(layoutRes, container, false)

    override fun getTheme() = when (mSettings.theme) {
        R.style.AppTheme_Dark,
        R.style.AppTheme_Dark_Pixel,
        R.style.AppTheme_Black,
        R.style.AppTheme_Black_Pixel -> R.style.BottomSheetDialog_Dark
        else -> R.style.BottomSheetDialog_Light
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
            BottomSheetDialog(requireContext(), theme)

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        dismissListener()
        mScope.close()
    }
}