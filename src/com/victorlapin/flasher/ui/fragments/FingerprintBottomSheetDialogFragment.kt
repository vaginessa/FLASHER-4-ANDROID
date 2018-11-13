package com.victorlapin.flasher.ui.fragments

import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.mtramin.rxfingerprint.RxFingerprint
import com.mtramin.rxfingerprint.data.FingerprintResult
import com.victorlapin.flasher.R
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_fingerprint.*
import timber.log.Timber

abstract class FingerprintBottomSheetDialogFragment : RoundedBottomSheetDialogFragment() {
    override val layoutRes = R.layout.fragment_fingerprint

    var successListener: () -> Unit = {}
    var cancelListener: () -> Unit = {}

    @StringRes
    private var mTitle: Int? = null
    @StringRes
    private var mSubtitle: Int? = null
    @StringRes
    private var mDescription: Int? = null

    private var mAuthDisposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mTitle = arguments?.getInt(ARG_TITLE)
        mSubtitle = arguments?.getInt(ARG_SUBTITLE)
        mDescription = arguments?.getInt(ARG_DESCRIPTION)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isCancelable = false

        if (mTitle!! > 0) {
            lbl_title.text = view.context.getString(mTitle!!)
        }
        if (mSubtitle!! > 0) {
            lbl_subtitle.text = view.context.getString(mSubtitle!!)
        }
        if (mDescription!! > 0) {
            lbl_description.text = view.context.getString(mDescription!!)
        }
        btn_cancel.setOnClickListener {
            cancelListener()
            dismiss()
        }
        val logo = view.context.packageManager.getApplicationIcon(view.context.packageName)
        img_logo.setImageDrawable(logo)

        mAuthDisposable = RxFingerprint.authenticate(view.context)
                .subscribe({
                    when (it.result!!) {
                        FingerprintResult.AUTHENTICATED -> {
                            img_fingerprint.backgroundTintList = ColorStateList
                                    .valueOf(ContextCompat.getColor(view.context, R.color.green))
                            lbl_status.setTextColor(img_fingerprint.backgroundTintList)
                            lbl_status.text = view.context.getString(R.string.success)
                            Handler().postDelayed({
                                successListener()
                                dismiss()
                            }, 250)
                        }
                        FingerprintResult.HELP -> {
                            img_fingerprint.backgroundTintList = ColorStateList
                                    .valueOf(ContextCompat.getColor(view.context, R.color.red))
                            lbl_status.setTextColor(img_fingerprint.backgroundTintList)
                            lbl_status.text = it.message
                        }
                        FingerprintResult.FAILED -> {
                            img_fingerprint.backgroundTintList = ColorStateList
                                    .valueOf(ContextCompat.getColor(view.context, R.color.red))
                            lbl_status.setTextColor(img_fingerprint.backgroundTintList)
                            lbl_status.text = view.context.getString(R.string.fingerprint_auth_error)
                        }
                    }
                }, {
                    img_fingerprint.backgroundTintList = ColorStateList
                            .valueOf(ContextCompat.getColor(view.context, R.color.red))
                    lbl_status.setTextColor(img_fingerprint.backgroundTintList)
                    lbl_status.text = it.localizedMessage ?: it.message
                    Timber.e(it, "Fingerprint auth failed")
                })
    }

    override fun onDestroyView() {
        mAuthDisposable?.dispose()
        mAuthDisposable = null
        super.onDestroyView()
    }

    companion object {
        const val ARG_TITLE = "ARG_TITLE"
        const val ARG_SUBTITLE = "ARG_SUBTITLE"
        const val ARG_DESCRIPTION = "ARG_DESCRIPTION"
    }
}