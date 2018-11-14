package com.victorlapin.flasher.ui

import android.os.Handler
import java.util.concurrent.Executor

class HandlerExecutor : Executor {
    private val mHandler = Handler()

    override fun execute(command: Runnable?) {
        mHandler.post(command)
    }
}