package com.victorlapin.flasher.model

import com.victorlapin.flasher.model.database.entity.Command

data class AboutClickEventArgs(val screenKey: String, val data: Any?)

data class EventArgs(
        val isSuccess: Boolean,
        val message: String? = null,
        val messageId: Int? = null
)

data class CommandClickEventArgs(val command: Command, val argsType: Int) {
    companion object {
        const val ARG1 = 1
        const val ARG2 = 2
    }
}