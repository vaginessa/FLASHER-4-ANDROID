package com.victorlapin.flasher.model.repository

import timber.log.Timber
import java.io.File
import java.util.*

class BackupsRepository {
    fun deleteObsoleteBackups(backupsPath: String, backupsToKeep: Int) {
        Timber.i("Going to delete old backups, will keep $backupsToKeep")
        val backupsFolder = File(backupsPath)
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
    }
}