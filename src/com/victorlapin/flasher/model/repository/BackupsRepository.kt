package com.victorlapin.flasher.model.repository

import com.victorlapin.flasher.Const
import com.victorlapin.flasher.manager.SettingsManager
import java.io.File
import java.util.*

class BackupsRepository(
        private val mSettings: SettingsManager
) {
    fun deleteObsoleteBackups() {
        if (mSettings.deleteObsoleteBackups) {
            var backupsToKeep = mSettings.backupsToKeep
            if (backupsToKeep <= 0) backupsToKeep = 1
            val twrpFolder = File(Const.TWRP_FOLDER)
            if (twrpFolder.exists()) {
                val backupsFolder = File(twrpFolder, "BACKUPS")
                if (backupsFolder.exists()) {
                    val profiles = backupsFolder.listFiles()
                    if (profiles != null && profiles.isNotEmpty()) {
                        val backups = profiles[0].listFiles { file, name ->
                            name.endsWith("_Flasher") && File(file, name).isDirectory
                        }
                        if (backups.isNotEmpty() && backups.size > backupsToKeep - 1) {
                            Arrays.sort(backups) { f1, f2 ->
                                val dateDiff = f2.lastModified() - f1.lastModified()
                                when {
                                    (dateDiff < 0) -> -1
                                    (dateDiff > 0) -> 1
                                    else -> 0
                                }
                            }
                            backups.drop(backupsToKeep - 1)
                            backups.forEach { it.deleteRecursively() }
                        }
                    }
                }
            }
        }
    }
}