package com.victorlapin.flasher.model.repository

import com.victorlapin.flasher.Const
import timber.log.Timber
import java.io.File
import java.util.*

class BackupsRepository {
    fun deleteObsoleteBackups(backupsToKeep: Int) {
        Timber.i("Going to delete old backups, will keep $backupsToKeep")
        val twrpFolder = File(Const.TWRP_FOLDER)
        if (twrpFolder.exists()) {
            val backupsFolder = File(twrpFolder, "BACKUPS")
            if (backupsFolder.exists()) {
                val profiles = backupsFolder.listFiles()
                if (profiles != null && profiles.isNotEmpty()) {
                    val backups = profiles[0].listFiles { file, name ->
                        name.endsWith("_Flasher") && File(file, name).isDirectory
                    }
                    if (backups.isNotEmpty()) {
                        if (backups.size > backupsToKeep - 1) {
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
                        } else {
                            Timber.i("Found ${backups.size} backups, no need to delete anything")
                        }
                    } else {
                        Timber.w("No backups created by Flasher found")
                    }
                } else {
                    Timber.w("Backup profiles can't be found (file listing is null or empty)")
                }
            } else {
                Timber.w("Folder '${backupsFolder.absolutePath}' can't be found")
            }
        } else {
            Timber.w("Folder '${twrpFolder.absolutePath}' can't be found")
        }
    }
}