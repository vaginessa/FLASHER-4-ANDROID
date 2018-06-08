package com.victorlapin.flasher.model.repository

import android.os.Environment
import com.topjohnwu.superuser.Shell
import com.topjohnwu.superuser.io.SuFile
import com.topjohnwu.superuser.io.SuFileOutputStream
import com.victorlapin.flasher.R
import com.victorlapin.flasher.manager.SettingsManager
import com.victorlapin.flasher.model.EventArgs
import com.victorlapin.flasher.model.database.entity.Command
import io.reactivex.Single
import java.io.File
import java.util.*

class RecoveryScriptRepository constructor(
        private val mCommandRepo: CommandsRepository,
        private val mSettings: SettingsManager
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
                                    result.appendln("backup $partString")
                                }
                            }
                            Command.TYPE_FLASH_FILE -> it.arg1?.let {
                                result.appendln("install $it")
                            }
                            Command.TYPE_FLASH_MASK -> if (it.arg1 != null && it.arg2 != null) {
                                val files = File(it.arg2).listFiles { file: File ->
                                    file.name.contains(Regex.fromLiteral(it.arg1!!))
                                }
                                if (files.isNotEmpty()) {
                                    Arrays.sort(files) { f1, f2 ->
                                        val dateDiff = f2.lastModified() - f1.lastModified()
                                        when {
                                            (dateDiff < 0) -> -1
                                            (dateDiff > 0) -> 1
                                            else -> 0
                                        }
                                    }
                                    result.appendln("install ${files[0].absolutePath}")
                                }
                            }
                        }
                    }
                    if (mSettings.saveDebugScript) {
                        saveDebugScript(result.toString())
                    }
                    emitter.onSuccess(result.toString())
                }
    }

    fun deployScript(script: String): EventArgs = if (Shell.rootAccess()) {
        try {
            SuFileOutputStream(SCRIPT_FILENAME).use {
                it.write(script.toByteArray())
            }
            EventArgs(isSuccess = true)
        } catch (ex: Exception) {
            ex.printStackTrace()
            EventArgs(isSuccess = false, message = ex.toString())
        }
    } else {
        mSuDeniedArgs
    }

    fun rebootRecovery(): EventArgs = if (Shell.rootAccess()) {
        Shell.Sync.su("svc power reboot recovery")
        EventArgs(isSuccess = true)
    } else {
        mSuDeniedArgs
    }

    private fun saveDebugScript(script: String) {
        try {
            val file = File(DEBUG_FILENAME)
            file.writeText(script)
        } catch (ignore: Exception) {
            ignore.printStackTrace()
        }
    }

    fun deleteScript() {
        if (Shell.rootAccess()) {
            val file = SuFile(SCRIPT_FILENAME)
            if (file.exists()) {
                file.delete()
            }
        }
    }

    private fun toArray(set: String) =
            set.split(",")
                    .map { it.trim().toLowerCase() }
                    .toTypedArray()

    private val mSuDeniedArgs =
            EventArgs(isSuccess = false, messageId = R.string.permission_denied_su)

    companion object {
        private const val SCRIPT_FILENAME = "/cache/recovery/openrecoveryscript"
        private val DEBUG_FILENAME = File(Environment.getExternalStorageDirectory(),
                "openrecoveryscript").absolutePath
    }
}