package com.victorlapin.flasher.model.repository

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import timber.log.Timber
import java.util.*

class BackupsRepository {
    fun deleteObsoleteBackups(context: Context, backupsPath: String?, backupsToKeep: Int) {
        Timber.i("Going to delete old backups, will keep $backupsToKeep")
        val backupsFolder = DocumentFile.fromTreeUri(context, Uri.parse(backupsPath))
        if (backupsFolder != null && backupsFolder.exists()) {
            val backups = backupsFolder.listFiles().filter { file ->
                file.name!!.endsWith("_Flasher") && file.isDirectory
            }.toTypedArray()
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
                        if (it.delete()) {
                            Timber.i("Successful")
                        } else {
                            Timber.w("Fail")
                        }
                    }
                } else {
                    Timber.i("Found ${backups.size} backups, no need to delete anything")
                }
            } else {
                Timber.w("No backups created by Flasher found")
            }
        } else {
            Timber.w("Folder '$backupsPath' can't be found")
        }
    }
}