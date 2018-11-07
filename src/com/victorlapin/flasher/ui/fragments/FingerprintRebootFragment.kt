package com.victorlapin.flasher.ui.fragments

import android.os.Bundle
import com.victorlapin.flasher.R

class FingerprintRebootFragment : FingerprintBottomSheetDialogFragment() {
    companion object {
        fun newInstance(
                successListener: () -> Unit = {},
                cancelListener: () -> Unit = {}
        ): FingerprintRebootFragment {
            val fragment = FingerprintRebootFragment()
            val args = Bundle()
            args.putInt(ARG_TITLE, R.string.fingerprint_auth_title)
            args.putInt(ARG_SUBTITLE, R.string.fingerprint_reboot_subtitle)
            args.putInt(ARG_DESCRIPTION, R.string.fingerprint_reboot_description)
            fragment.arguments = args
            fragment.successListener = successListener
            fragment.cancelListener = cancelListener
            return fragment
        }
    }
}