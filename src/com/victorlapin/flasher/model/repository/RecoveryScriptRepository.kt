package com.victorlapin.flasher.model.repository

import com.topjohnwu.superuser.io.SuFile
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

    fun deployScript(script: String): EventArgs {
        try {
            val file = SuFile(SCRIPT_FILENAME)
            file.writeText(script)
            return EventArgs(isSuccess = true)
        } catch (ex: Exception) {
            ex.printStackTrace()
            return EventArgs(isSuccess = false, message = ex.toString())
        }
    }

    private fun toArray(set: String) =
            set.split(",")
                    .map { it.trim().toLowerCase() }
                    .toTypedArray()

    companion object {
        private const val SCRIPT_FILENAME = "/cache/recovery/openrecoveryscript"
    }
}