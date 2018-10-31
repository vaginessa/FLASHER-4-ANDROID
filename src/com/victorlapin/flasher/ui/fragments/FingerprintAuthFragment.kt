package com.victorlapin.flasher.ui.fragments

import android.os.Bundle
import com.victorlapin.flasher.R

class FingerprintAuthFragment : FingerprintBottomSheetDialogFragment() {
    companion object {
        fun newInstance(
                successListener: () -> Unit = {},
                cancelListener: () -> Unit = {}
        ): FingerprintAuthFragment {
            val fragment = FingerprintAuthFragment()
            val args = Bundle()
            args.putInt(ARG_TITLE, R.string.fingerprint_auth_title)
            args.putInt(ARG_SUBTITLE, R.string.fingerprint_auth_subtitle)
            args.putInt(ARG_DESCRIPTION, R.string.fingerprint_auth_description)
            fragment.arguments = args
            fragment.successListener = successListener
            fragment.cancelListener = cancelListener
            return fragment
        }
    }
}