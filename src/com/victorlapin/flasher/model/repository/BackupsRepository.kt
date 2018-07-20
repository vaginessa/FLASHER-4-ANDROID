package com.victorlapin.flasher.model.repository

import com.victorlapin.flasher.Const
import timber.log.Timber
import java.io.File
import java.util.*

class BackupsRepository {
    fun deleteObsoleteBackups(backupsToKeep: Int) {
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
                        val backupsToDelete = backups.drop(backupsToKeep - 1)
                        backupsToDelete.forEach {
                            Timber.i("Deleting backup: ${it.name}")
                            it.deleteRecursively()
                            Timber.i("Successful")
                        }
                    }
                }
            }
        }
    }
}