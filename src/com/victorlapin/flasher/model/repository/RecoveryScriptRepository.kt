package com.victorlapin.flasher.model.repository

import android.os.Environment
import android.os.StatFs
import com.topjohnwu.superuser.Shell
import com.topjohnwu.superuser.io.SuFile
import com.topjohnwu.superuser.io.SuFileOutputStream
import com.victorlapin.flasher.R
import com.victorlapin.flasher.manager.SettingsManager
import com.victorlapin.flasher.model.BuildScriptResult
import com.victorlapin.flasher.model.EventArgs
import com.victorlapin.flasher.model.database.entity.Command
import io.reactivex.Single
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class RecoveryScriptRepository constructor(
        private val mCommandRepo: CommandsRepository,
        private val mSettings: SettingsManager
) {
    fun buildScript(chainId: Long): Single<BuildScriptResult> = Single.create { emitter ->
        mCommandRepo.getCommands(chainId)
                .subscribe {
                    val scriptBuilder = StringBuilder()
                    val resolvedFilesBuilder = StringBuilder()
                    it.forEach {
                        when (it.type) {
                            Command.TYPE_WIPE -> it.arg1?.let {
                                val partitions = toArray(it)
                                        .map { if (it == "dalvik-cache") "dalvik" else it }
                                partitions.forEach {
                                    scriptBuilder.appendln("wipe $it")
                                }
                            }
                            Command.TYPE_BACKUP -> it.arg1?.let {
                                val partitions = toArray(it)
                                val partString = StringBuilder()
                                partitions.forEach {
                                    partString.append(it[0].toUpperCase())
                                }
                                if (partString.isNotEmpty()) {
                                    val dt = SimpleDateFormat("YYYY-MM-dd_HH-mm-ss",
                                            Locale.getDefault()).format(Date())
                                    val out = Shell.Sync.sh("getprop ro.build.id")
                                    val buildId = if (out.isNotEmpty()) out[0] else ""
                                    val backupName = if (buildId.isNotEmpty()) "${dt}_$buildId" else dt
                                    scriptBuilder.appendln("backup $partString $backupName")
                                }
                            }
                            Command.TYPE_FLASH_FILE -> it.arg1?.let {
                                scriptBuilder.appendln("install $it")
                            }
                            Command.TYPE_FLASH_MASK -> if (it.arg1 != null && it.arg2 != null) {
                                val files = File(it.arg2).listFiles { file: File ->
                                    file.name.contains(Regex.fromLiteral(it.arg1!!))
                                            && file.extension.toLowerCase() == "zip"
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
                                    scriptBuilder.appendln("install ${files[0].absolutePath}")
                                    resolvedFilesBuilder.appendln("${it.arg1} -> ${files[0].name}")
                                }
                            }
                        }
                    }
                    val result = BuildScriptResult(
                            script = scriptBuilder.toString(),
                            resolvedFiles = resolvedFilesBuilder.toString())
                    if (mSettings.saveDebugScript) {
                        saveDebugScript(result.script)
                    }
                    emitter.onSuccess(result)
                }
    }

    fun deployScript(script: String): EventArgs {
        if (mSettings.useAnalyzer) {
            val analyzeResult = analyzeScript(script)
            if (analyzeResult != null) {
                return analyzeResult
            }
        }

        return if (Shell.rootAccess()) {
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

    private fun analyzeScript(script: String): EventArgs? {
        // check for emptiness
        if (script.isBlank()) {
            return EventArgs(isSuccess = false, messageId = R.string.analyze_empty_script)
        }

        // check for possible no rom
        val indexWipe = script.lastIndexOf("wipe system")
        val indexFlash = script.lastIndexOf("install ")
        if (indexWipe > indexFlash) {
            return EventArgs(isSuccess = false, messageId = R.string.analyze_no_rom)
        }

        // check for system space
        if (indexFlash >= 0) {
            val systemSpace = if (script.contains("wipe system"))
                StatFs("/system").totalBytes else StatFs("/system").freeBytes
            var zipSpace = 0L
            script.split("\n")
                    .filter { it.startsWith("install ") }
                    .map { it -> it.replace("install ", "") }
                    .forEach { zipSpace += File(it).length() }
            if (zipSpace > systemSpace) {
                return EventArgs(isSuccess = false, messageId = R.string.analyze_system_space)
            }
        }

        return null
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