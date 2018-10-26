package com.victorlapin.flasher.ui.fragments

import android.content.DialogInterface
import android.os.Bundle
import android.support.annotation.StringRes
import android.view.View
import com.mtramin.rxfingerprint.RxFingerprint
import com.mtramin.rxfingerprint.data.FingerprintResult
import com.victorlapin.flasher.R
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_fingerprint.*
import timber.log.Timber

abstract class FingerprintBottomSheetDialogFragment : RoundedBottomSheetDialogFragment() {
    override val layoutRes = R.layout.fragment_fingerprint

    private val mCancelSubject: PublishSubject<Any> = PublishSubject.create()
    val cancelEvent: PublishSubject<Any> = mCancelSubject
    private val mSuccessSubject: PublishSubject<Any> = PublishSubject.create()
    val successEvent: PublishSubject<Any> = mSuccessSubject

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
            mCancelSubject.onNext(Any())
            dismiss()
        }
        val logo = view.context.packageManager.getApplicationIcon(view.context.packageName)
        img_logo.setImageDrawable(logo)

        mAuthDisposable = RxFingerprint.authenticate(view.context)
                .subscribe({
                    when (it.result!!) {
                        FingerprintResult.AUTHENTICATED -> {
                            lbl_status.text = ""
                            mSuccessSubject.onNext(Any())
                            dismiss()
                        }
                        FingerprintResult.HELP -> lbl_status.text = it.message
                        FingerprintResult.FAILED ->
                            lbl_status.text = view.context.getString(R.string.fingerprint_auth_error)
                    }
                }, {
                    lbl_status.text = it.localizedMessage
                    Timber.e(it, "Fingerprint auth failed")
                })
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        mAuthDisposable?.dispose()
    }

    companion object {
        const val ARG_TITLE = "ARG_TITLE"
        const val ARG_SUBTITLE = "ARG_SUBTITLE"
        const val ARG_DESCRIPTION = "ARG_DESCRIPTION"
    }
}