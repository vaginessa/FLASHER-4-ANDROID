package com.victorlapin.flasher.ui

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Handler
import androidx.annotation.StringRes
import androidx.biometric.BiometricConstants
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import timber.log.Timber
import java.util.concurrent.Executor

object Biometric {
    fun askFingerprint(
        activity: FragmentActivity,
        @StringRes title: Int,
        @StringRes description: Int? = null,
        successListener: () -> Unit = {},
        cancelListener: () -> Unit = {}
    ) {
        val oldOrientation = activity.requestedOrientation
        val newOrientation: Int
        if (oldOrientation == ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) {
            newOrientation = if (activity.resources.configuration.orientation ==
                Configuration.ORIENTATION_LANDSCAPE
            ) {
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            } else {
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
            activity.requestedOrientation = newOrientation
        }

        val builder = BiometricPrompt.PromptInfo.Builder().apply {
            setTitle(activity.getString(title))
            description?.let {
                setDescription(activity.getString(it))
            }
            setNegativeButtonText(activity.getString(android.R.string.cancel))
        }
        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                Timber.i("Fingerprint check success")
                successListener()
                activity.requestedOrientation = oldOrientation
            }

            @SuppressLint("SwitchIntDef")
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                Timber.i("Fingerprint check error: $errString")
                when (errorCode) {
                    BiometricConstants.ERROR_NEGATIVE_BUTTON,
                    BiometricConstants.ERROR_USER_CANCELED -> {
                        cancelListener()
                        activity.requestedOrientation = oldOrientation
                    }
                    else -> super.onAuthenticationError(errorCode, errString)
                }
            }
        }

        BiometricPrompt(activity, HandlerExecutor(), callback)
            .authenticate(builder.build())
    }

    fun cancelFingerprint(fm: FragmentManager) {
        fm.findFragmentByTag(BIOMETRIC_FRAGMENT_TAG)?.let {
            fm.beginTransaction().remove(it).commitAllowingStateLoss()
        }
        fm.findFragmentByTag(DIALOG_FRAGMENT_TAG)?.let {
            fm.beginTransaction().remove(it).commitAllowingStateLoss()
        }
        fm.findFragmentByTag(FINGERPRINT_HELPER_FRAGMENT_TAG)?.let {
            fm.beginTransaction().remove(it).commitAllowingStateLoss()
        }
    }

    private class HandlerExecutor : Executor {
        private val mHandler = Handler()

        override fun execute(command: Runnable?) {
            mHandler.post(command)
        }
    }

    private const val DIALOG_FRAGMENT_TAG = "FingerprintDialogFragment"
    private const val FINGERPRINT_HELPER_FRAGMENT_TAG = "FingerprintHelperFragment"
    private const val BIOMETRIC_FRAGMENT_TAG = "BiometricFragment"
}