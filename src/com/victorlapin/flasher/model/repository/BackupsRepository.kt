package com.victorlapin.flasher.model.repository

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.victorlapin.flasher.Const
import timber.log.Timber
import java.io.File
import java.util.*

class BackupsRepository {
    fun deleteObsoleteBackups(context: Context, backupsPath: String?, backupsToKeep: Int) {
        Timber.i("Going to delete old backups, will keep $backupsToKeep")
        if (backupsPath == null) {
            // use default internal storage path
            val backupsFolder = File(Const.TWRP_FOLDER, "BACKUPS")
            if (backupsFolder.exists()) {
                val profiles = backupsFolder.listFiles()
                if (profiles != null && profiles.isNotEmpty()) {
                    val backups = profiles[0].listFiles { file, name ->
                        name.endsWith("_Flasher") && file.isDirectory
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
                                if (it.deleteRecursively()) {
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
                    Timber.w("Backup profiles can't be found (file listing is null or empty)")
                }
            } else {
                Timber.w("Folder '${backupsFolder.absolutePath}' can't be found")
            }
        } else {
            // use custom document tree
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
}