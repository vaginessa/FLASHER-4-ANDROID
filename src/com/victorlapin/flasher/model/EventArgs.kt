package com.victorlapin.flasher.model

data class AboutClickEventArgs(val screenKey: String, val data: Any?)

data class EventArgs(
        val isSuccess: Boolean,
        val message: String? = null,
        val messageId: Int? = null
)