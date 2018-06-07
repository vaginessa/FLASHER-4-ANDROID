package com.victorlapin.flasher.model.repository

import com.topjohnwu.superuser.Shell
import com.topjohnwu.superuser.io.SuFile
import com.victorlapin.flasher.R
import com.victorlapin.flasher.model.EventArgs
import com.victorlapin.flasher.model.database.entity.Command
import io.reactivex.Single

class RecoveryScriptRepository constructor(
        private val mCommandRepo: CommandsRepository
) {
    fun buildScript(): Single<String> = Single.create { emitter ->
        mCommandRepo.getCommands()
                .subscribe {
                    val result = StringBuilder()
                    it.forEach {
                        when (it.type) {
                            Command.TYPE_WIPE -> it.arg1?.let {
                                val partitions = toArray(it)
                                        .map { if (it == "dalvik-cache") "dalvik" else it }
                                partitions.forEach {
                                    result.appendln("wipe $it")
                                }
                            }
                            Command.TYPE_BACKUP -> it.arg1?.let {
                                val partitions = toArray(it)
                                val partString = StringBuilder()
                                partitions.forEach {
                                    partString.append(it[0].toUpperCase())
                                }
                                if (partString.isNotEmpty()) {
                                    result.appendln("backup ${partString}M")
                                }
                            }
                            Command.TYPE_FLASH -> it.arg1?.let {
                                result.appendln("install $it")
                            }
                        }
                    }
                    emitter.onSuccess(result.toString())
                }
    }

    fun deployScript(script: String): EventArgs = if (Shell.rootAccess()) {
        try {
            val file = SuFile(SCRIPT_FILENAME)
            file.writeText(script)
            EventArgs(isSuccess = true)
        } catch (ex: Exception) {
            ex.printStackTrace()
            EventArgs(isSuccess = false, message = ex.toString())
        }
    } else {
        EventArgs(isSuccess = false, messageId = R.string.permission_denied_su)
    }

    private fun toArray(set: String) =
            set.split(",")
                    .map { it.trim().toLowerCase() }
                    .toTypedArray()

    companion object {
        private const val SCRIPT_FILENAME = "/cache/recovery/openrecoveryscript"
    }
}