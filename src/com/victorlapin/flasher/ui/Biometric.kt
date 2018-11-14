package com.victorlapin.flasher.ui

import android.annotation.SuppressLint
import android.os.Handler
import androidx.annotation.StringRes
import androidx.biometrics.BiometricConstants
import androidx.biometrics.BiometricPrompt
import androidx.fragment.app.FragmentActivity
import timber.log.Timber
import java.util.concurrent.Executor

object Biometric {
    fun askFingerprint(
            activity: FragmentActivity,
            @StringRes title: Int,
            @StringRes description: Int,
            successListener: () -> Unit = {},
            cancelListener: () -> Unit = {}
            ) {
        val builder = BiometricPrompt.PromptInfo.Builder().apply {
            setTitle(activity.getString(title))
            setDescription(activity.getString(description))
            setNegativeButtonText(activity.getString(android.R.string.cancel))
        }
        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                Timber.i("Fingerprint check success")
                successListener()
            }

            @SuppressLint("SwitchIntDef")
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                Timber.i("Fingerprint check error: $errString")
                when (errorCode) {
                    BiometricConstants.ERROR_NEGATIVE_BUTTON,
                    BiometricConstants.ERROR_USER_CANCELED -> cancelListener()
                    else -> super.onAuthenticationError(errorCode, errString)
                }
            }
        }

        BiometricPrompt(activity, HandlerExecutor(), callback)
                .authenticate(builder.build())
    }

    private class HandlerExecutor : Executor {
        private val mHandler = Handler()

        override fun execute(command: Runnable?) {
            mHandler.post(command)
        }
    }
}