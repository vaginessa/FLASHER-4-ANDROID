package com.victorlapin.flasher.model

import com.victorlapin.flasher.model.database.entity.Command
import ru.terrakok.cicerone.Screen

data class AboutClickEventArgs(val screen: Screen)

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