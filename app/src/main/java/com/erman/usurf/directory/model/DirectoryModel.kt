package com.erman.usurf.directory.model

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.documentfile.provider.DocumentFile
import com.erman.usurf.MainApplication.Companion.appContext
import com.erman.usurf.R
import com.erman.usurf.directory.utils.SIMPLE_DATE_FORMAT_PATTERN
import com.erman.usurf.utils.SHARED_PREF_FILE
import java.io.*
import java.text.SimpleDateFormat
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

class DirectoryModel {
    @SuppressLint("SimpleDateFormat")
    private val dateFormat = SimpleDateFormat(SIMPLE_DATE_FORMAT_PATTERN)

    fun getFileModelsFromFiles(path: String): List<FileModel> {
        val files = File(path).listFiles().toList()
        return files.map {
            FileModel(it.path, it.name, getConvertedFileSize(it.length()), it.isDirectory,
                dateFormat.format(it.lastModified()), it.extension,
                (it.listFiles()?.size.toString() + " files"), false)
        }
    }

    fun getFolderSize(path: String): Double {
        if (File(path).exists()) {
            var size = 0.0
            var fileList = File(path).listFiles().toList()

            for (i in fileList.indices) {
                if (fileList[i].isDirectory)
                    size += getFolderSize(fileList[i].path)
                else size += fileList[i].length()
            }
            return size
        }
        return 0.0
    }

    private fun getConvertedFileSize(size: Long): String {
        var sizeStr = ""

        val kilobyte = size / 1024.0
        val megabyte = size / (1024.0 * 1024.0)
        val gigabyte = size / (1024.0 * 1024.0 * 1024.0)
        val terabyte = size / (1024.0 * 1024.0 * 1024.0 * 1024.0)

        sizeStr = when {
            terabyte > 1 -> "%.2f".format(terabyte) + " TB"
            gigabyte > 1 -> "%.2f".format(gigabyte) + " GB"
            megabyte > 1 -> "%.2f".format(megabyte) + " MB"
            kilobyte > 1 -> "%.2f".format(kilobyte) + " KB"
            else -> size.toDouble().toString() + " Bytes"
        }
        return sizeStr
    }

    fun getConvertedFileSize(size: Double): String {
        var sizeStr = ""

        val kilobyte = size / 1024.0
        val megabyte = size / (1024.0 * 1024.0)
        val gigabyte = size / (1024.0 * 1024.0 * 1024.0)
        val terabyte = size / (1024.0 * 1024.0 * 1024.0 * 1024.0)

        sizeStr = when {
            terabyte > 1 -> "%.2f".format(terabyte) + " TB"
            gigabyte > 1 -> "%.2f".format(gigabyte) + " GB"
            megabyte > 1 -> "%.2f".format(megabyte) + " MB"
            kilobyte > 1 -> "%.2f".format(kilobyte) + " KB"
            else -> size.toDouble().toString() + " Bytes"
        }
        return sizeStr
    }

    fun openFile(directory: FileModel) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = FileProvider.getUriForFile(appContext, appContext.packageName, File(directory.path))
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION.or(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        appContext.startActivity(intent)
    }

    fun manageMultipleSelectionList(file: FileModel, multipleSelection: MutableList<FileModel>): MutableList<FileModel> {
        if (file.isSelected) {
            file.isSelected = false
            multipleSelection.remove(file)
        } else {
            file.isSelected = true
            multipleSelection.add(file)
        }
        return multipleSelection
    }

    fun clearMultipleSelection(multipleSelection: MutableList<FileModel>): MutableList<FileModel> {
        for (i in multipleSelection.indices) {
            multipleSelection[i].isSelected = false
        }
        multipleSelection.clear()
        return multipleSelection
    }


    //fun getDocumentFile(file: File, isDirectory: Boolean, context: Context): DocumentFile? {
    //    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) return DocumentFile.fromFile(file)
//
    //    val getExtSdCardBaseFolder = getStorageDirectories(
    //        context
    //    ).elementAt(1)
    //    var originalDirectory = false
//
    //    var relativePathOfFile: String? = null
    //    try {
    //        val fullPath = file.canonicalPath
    //        if (getExtSdCardBaseFolder != fullPath) relativePathOfFile = fullPath.substring(getExtSdCardBaseFolder.length + 1)
    //        else originalDirectory = true
    //    } catch (e: IOException) {
    //        return null
    //    } catch (f: Exception) {
    //        originalDirectory = true
    //    }
//
    //    val extSdCardChosenUri = context.getSharedPreferences(SHARED_PREF_FILE, Context.MODE_PRIVATE).getString("extSdCardChosenUri", null)
//
    //    var treeUri: Uri? = null
    //    if (extSdCardChosenUri != null) treeUri = Uri.parse(extSdCardChosenUri)
    //    if (treeUri == null) {
    //        return null
    //    }
//
    //    // start with root of SD card and then parse through document tree.
    //    var document = DocumentFile.fromTreeUri(context, treeUri)
    //    if (originalDirectory) return document
    //    val parts = relativePathOfFile!!.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
//
    //    for (i in parts.indices) {
    //        var nextDocument = document!!.findFile(parts[i])
//
    //        if (nextDocument == null) {
    //            nextDocument = if (i < parts.size - 1 || isDirectory) {
    //                document.createDirectory(parts[i])
    //            } else {
    //                document.createFile("*/*", parts[i])
    //            }
    //        }
    //        document = nextDocument
    //    }
    //    return document
    //}

    //fun getSearchedDirectoryFiles(path: String, searchQuery: String): List<File> {
    //    try {
    //        return File(path).listFiles()!!.filter { file ->
    //            searchQuery.decapitalize().toRegex().containsMatchIn(file.nameWithoutExtension.decapitalize())
    //        }.toList()
    //    } catch (err: Exception) {
    //        Log.e("getSearchedDirFiles", err.toString())
    //    }
    //    return emptyList()
    //}

    //fun getSearchedDeviceFiles(storagePaths: ArrayList<String>, searchQuery: String): List<File> {
    //    val fileList = mutableListOf<File>()
    //    try {
    //        for (i in storagePaths.indices) {
    //            Log.e("strg", storagePaths[i])
    //            fileList.addAll(
    //                getSubSearchedFiles(
    //                    File(
    //                        storagePaths[i]
    //                    ), searchQuery
    //                )
    //            )
    //        }
//
    //        return fileList.filter { file ->
    //            searchQuery.decapitalize().toRegex().containsMatchIn(file.nameWithoutExtension.decapitalize())
    //        }
    //    } catch (err: Exception) {
    //        Log.e("getSearchedDeviceFiles", err.toString())
    //    }
    //    return emptyList()
    //}

    //fun getSubSearchedFiles(directory: File, searchQuery: String, res: MutableSet<File> = mutableSetOf<File>()): Set<File> {
    //    //Depth first search algorithm
//
    //    for (file in directory.listFiles()!!.toSet()) {
    //        if (file.isDirectory) {
    //            getSubSearchedFiles(file, searchQuery, res)
    //        } else {
    //            res.add(file)
    //        }
    //        res.addAll(directory.listFiles()!!.toSet())
    //    }
    //    return res
    //}

    fun rename(context: Context, selectedDirectories: List<File>, newNameToBe: String, isExtSdCard: Boolean, updateFragment: () -> Unit) {
        var isSuccess = false

        if (isExtSdCard) {
            for (i in selectedDirectories.indices) {
                var newFileName = newNameToBe
                if (i > 0) newFileName = newFileName + "(" + i + ")"
                //isSuccess = getDocumentFile(
                //    selectedDirectories[i],
                //    selectedDirectories[i].isDirectory,
                //    context
                //)!!.renameTo(newFileName)
            }
        } else {
            for (i in selectedDirectories.indices) {
                val dirName = selectedDirectories[i].path.removeSuffix(selectedDirectories[i].name)
                var newFileName = newNameToBe

                if (i > 0) newFileName = newFileName + "(" + i + ")"

                if (!selectedDirectories[i].isDirectory) {
                    newFileName = newFileName + "." + selectedDirectories[i].extension
                }
                val prev = File(dirName, selectedDirectories[i].name)
                val new = File(dirName, newFileName)
                isSuccess = prev.renameTo(new)

                if (!isSuccess) {
                    Toast.makeText(context, context.getString(R.string.error_while_renaming) + prev.name, Toast.LENGTH_LONG).show()
                    break
                }
            }
        }
        if (isSuccess) {
            Toast.makeText(context, context.getString(R.string.renaming_successful), Toast.LENGTH_LONG).show()
            updateFragment.invoke()
        }
    }

    fun deleteFolderRecursively(documentFile: DocumentFile): Boolean {
        if (documentFile.listFiles().isNotEmpty()) {
            for (i in documentFile.listFiles().size - 1 downTo 0) {
                deleteFolderRecursively(documentFile.listFiles()[i])
            }
        }
        if (documentFile.delete()) return true

        return false
    }

    fun delete(context: Context, selectedDirectories: List<File>, isExtSdCard: Boolean, updateFragment: () -> Unit) {
        var isSuccess = false

        if (isExtSdCard) {
            for (i in selectedDirectories.indices) {
                //isSuccess = deleteFolderRecursively(
                //    getDocumentFile(
                //        selectedDirectories[i],
                //        selectedDirectories[i].isDirectory,
                //        context
                //    )!!
                //)
            }
        } else {
            for (i in selectedDirectories.indices) {
                isSuccess = if (selectedDirectories[i].isDirectory) {
                    File(selectedDirectories[i].path).deleteRecursively()
                } else {
                    File(selectedDirectories[i].path).delete()
                }
                if (!isSuccess) {
                    Toast.makeText(context, context.getString(R.string.error_while_deleting) + selectedDirectories[i].name, Toast.LENGTH_LONG).show()
                    break
                }
            }
        }
        if (isSuccess) {
            updateFragment.invoke()
            Toast.makeText(context, context.getString(R.string.deleting_successful), Toast.LENGTH_LONG).show()
        }
    }

    fun createFolder(context: Context, path: String, folderName: String, isExtSdCard: Boolean, updateFragment: () -> Unit) {
        var isSuccess = false
        if (isExtSdCard) {
            //getDocumentFile(
            //    File(path),
            //    File(path).isDirectory,
            //    context
            //)!!.createDirectory(folderName)

            if (File(path + "/" + folderName).isDirectory) isSuccess = true
        } else {
            isSuccess = File(path + "/" + folderName).mkdir()
        }
        if (isSuccess) {
            Toast.makeText(context, context.getString(R.string.folder_creation_successful) + folderName, Toast.LENGTH_LONG).show()

            updateFragment.invoke()
        } else Toast.makeText(context, context.getString(R.string.error_when_creating_folder) + folderName, Toast.LENGTH_LONG).show()
    }

    fun createFile(context: Context, path: String, folderName: String, isExtSdCard: Boolean, updateFragment: () -> Unit) {
        var isSuccess = false
        if (isExtSdCard) {
            //getDocumentFile(
            //    File(path),
            //    File(path).isDirectory,
            //    context
            //)!!.createFile("*/*", folderName)

            if (File(path + "/" + folderName).isFile) isSuccess = true
        } else {
            isSuccess = File(path + "/" + folderName).createNewFile()
        }
        if (isSuccess) {
            Toast.makeText(context, context.getString(R.string.file_creation_successful) + folderName, Toast.LENGTH_LONG).show()

            updateFragment.invoke()
        } else Toast.makeText(context, context.getString(R.string.error_when_creating_file) + folderName, Toast.LENGTH_LONG).show()
    }

    fun copyFile(context: Context, copyOrMoveSources: List<File>, copyOrMoveDestination: String, isExtSdCard: Boolean, updateFragment: () -> Unit) {
        var isSuccess = false

        if (isExtSdCard) {
            for (i in copyOrMoveSources.indices) {
                copyToExtCard(
                    copyOrMoveSources[i],
                    copyOrMoveDestination,
                    context
                )
            }
        } else {

            for (i in copyOrMoveSources.indices) {
                if (copyOrMoveSources[i].isDirectory) {
                    isSuccess = File(copyOrMoveSources[i].path).copyRecursively(File(copyOrMoveDestination + File.separator + copyOrMoveSources[i].name))
                } else {
                    try {
                        File(copyOrMoveSources[i].path).copyTo(File(copyOrMoveDestination + File.separator + copyOrMoveSources[i].name))
                    } catch (err: IOException) {
                        isSuccess = false
                        break
                    }
                    isSuccess = true
                }
            }
        }
        if (isSuccess) {
            Toast.makeText(context, context.getString(R.string.copy_successful), Toast.LENGTH_LONG).show()
            updateFragment.invoke()
        } else {
            Toast.makeText(context, context.getString(R.string.error_while_copying), Toast.LENGTH_LONG).show()
        }
    }

    fun copyToExtCard(sourceFile: File, copyOrMoveDestination: String?, context: Context): Boolean {
        //var documentFileDestination: DocumentFile = getDocumentFile(
        //    File(copyOrMoveDestination!!),
        //    File(copyOrMoveDestination).isDirectory,
        //    context
        //)!!
        var fileInputStream: FileInputStream? = null
        var outputStream: OutputStream? = null

        if (sourceFile.isDirectory) {
            //documentFileDestination.createDirectory(sourceFile.name)!!

            for (i in sourceFile.listFiles()!!.indices) {
                copyToExtCard(
                    sourceFile.listFiles()!![i],
                    copyOrMoveDestination + File.separator + sourceFile.name,
                    context
                )
            }
        } else {
            //documentFileDestination = documentFileDestination.createFile(sourceFile.extension, sourceFile.name)!!
            //try {
            //    fileInputStream = FileInputStream(sourceFile)
            //    outputStream = context.contentResolver.openOutputStream(documentFileDestination.uri)!!
//
            //    val buffer = 6144
            //    val byteArray = ByteArray(buffer)
            //    var bytesRead: Int
            //    try {
            //        while (fileInputStream.read(byteArray).also { bytesRead = it } != -1) {
            //            outputStream.write(byteArray, 0, bytesRead)
            //        }
            //    } catch (err: Exception) {
            //        err.printStackTrace()
            //    } finally {
            //        try {
            //            fileInputStream.close()
            //            outputStream.close()
            //            return true
            //        } catch (err: Exception) {
            //            err.printStackTrace()
            //        }
            //    }
            //} catch (err: Exception) {
            //    err.printStackTrace()
            //} finally {
            //    try {
            //        fileInputStream!!.close()
            //        outputStream!!.close()
            //        return true
            //    } catch (err: Exception) {
            //        err.printStackTrace()
            //    }
            //}
            return false
        }
        return false
    }

    fun moveFile(context: Context, copyOrMoveSources: List<File>, copyOrMoveDestination: String, isExtSdCard: Boolean, updateFragment: () -> Unit) {
        var isSuccess = false

        for (i in copyOrMoveSources.indices) {
            if (copyOrMoveSources[i].isDirectory) {
                isSuccess = File(copyOrMoveSources[i].path).copyRecursively(File(copyOrMoveDestination + File.separator + copyOrMoveSources[i].name))
            } else {
                try {
                    File(copyOrMoveSources[i].path).copyTo(File(copyOrMoveDestination + File.separator + copyOrMoveSources[i].name))
                } catch (err: IOException) {
                    isSuccess = false
                    break
                }
                isSuccess = true
            }
            if (copyOrMoveSources[i].isDirectory && isSuccess) {
                isSuccess = File(copyOrMoveSources[i].path).deleteRecursively()
            } else if (!copyOrMoveSources[i].isDirectory && isSuccess) {
                isSuccess = File(copyOrMoveSources[i].path).delete()
            }
        }
        if (isSuccess) {
            Toast.makeText(context, context.getString(R.string.moving_successful), Toast.LENGTH_LONG).show()
            updateFragment.invoke()
        } else {
            Toast.makeText(context, context.getString(R.string.error_while_moving), Toast.LENGTH_LONG).show()
        }
    }

    fun zipFile(context: Context, selectedDirectories: List<File>, zipName: String, isExtSdCard: Boolean, updateFragment: () -> Unit) {
        val buffer = 6144

        try {
            val destination = FileOutputStream(selectedDirectories[0].parent!! + File.separator + zipName + ".zip") //burası
            val output = ZipOutputStream(BufferedOutputStream(destination))
            val data = ByteArray(buffer)

            for (i in selectedDirectories.indices) {
                if (selectedDirectories[i].isDirectory) {
                    if (!zipFolder(
                            selectedDirectories[i].listFiles()!!.toList(),
                            output,
                            selectedDirectories[i].name
                        )
                    ) {
                        Toast.makeText(context, context.getString(R.string.error_while_compressing), Toast.LENGTH_LONG).show()
                        return  //in case of an error in zipFolder function
                    }
                } else {
                    val fileOrigin = BufferedInputStream(FileInputStream(selectedDirectories[i]))
                    output.putNextEntry(ZipEntry(selectedDirectories[i].name))
                    var counter = (fileOrigin.read(data, 0, buffer))

                    while (counter != -1) {
                        output.write(data, 0, counter)
                        counter = (fileOrigin.read(data, 0, buffer))
                    }
                    fileOrigin.close()
                }
            }
            output.close()
        } catch (err: Exception) {
            Toast.makeText(context, context.getString(R.string.error_while_compressing), Toast.LENGTH_LONG).show()
            Log.e("Error while compressing", err.toString())
            return
        }
        updateFragment.invoke()
        Toast.makeText(context, context.getString(R.string.compressing_successful), Toast.LENGTH_LONG).show()
    }

    fun zipFolder(selectedDirectories: List<File>, output: ZipOutputStream, folderName: String): Boolean {
        val buffer = 6144
        val data = ByteArray(buffer)

        try {
            for (i in selectedDirectories.indices) {
                if (selectedDirectories[i].isDirectory) {
                    zipFolder(
                        selectedDirectories[i].listFiles()!!.toList(),
                        output,
                        folderName + File.separator + selectedDirectories[i].name
                    )
                } else {
                    output.putNextEntry(ZipEntry(folderName + File.separator + selectedDirectories[i].name))
                    val fileOrigin = BufferedInputStream(FileInputStream(selectedDirectories[i]))

                    var counter = (fileOrigin.read(data, 0, buffer))
                    while (counter != -1) {
                        output.write(data, 0, counter)
                        counter = (fileOrigin.read(data, 0, buffer))
                    }
                    fileOrigin.close()
                }
            }
            return true
        } catch (err: Exception) {
            Log.e("Error while compressing", err.toString())
            return false
        }
    }

    fun unzip(context: Context, selectedDirectories: List<File>, updateFragment: () -> Unit) {
        val buffer = 6144
        val data = ByteArray(buffer)

        try {
            for (i in selectedDirectories.indices) {
                val baseFolderPath = selectedDirectories[i].parent!! + File.separator + selectedDirectories[i].nameWithoutExtension
                File(baseFolderPath).mkdir()

                val zipInput = ZipInputStream(FileInputStream(selectedDirectories[i].path))
                var zipContent: ZipEntry?

                while (zipInput.nextEntry.also { zipContent = it } != null) {
                    if (zipContent!!.name.contains(File.separator)) {    //if it contains directory
                        //zipContent!!.name -> "someSubFolder/zipContentName.extension"
                        createSubDirectories(
                            zipContent!!.name,
                            baseFolderPath
                        ) //create all the subdirectories
                    }
                    val fileOutput = FileOutputStream(baseFolderPath + File.separator + zipContent!!.name)

                    var counter: Int = zipInput.read(data, 0, buffer)
                    while (counter != -1) {
                        fileOutput.write(data, 0, counter)
                        counter = zipInput.read(data, 0, buffer)
                    }
                    zipInput.closeEntry()
                    fileOutput.close()
                }
                zipInput.close()
            }
            updateFragment.invoke()
        } catch (e: Exception) {
            Toast.makeText(context, context.getString(R.string.error_while_extracting), Toast.LENGTH_LONG).show()
            Log.e("Error while extracting", e.toString())
            return
        }
        Toast.makeText(context, context.getString(R.string.extracting_successful), Toast.LENGTH_LONG).show()
    }

    fun createSubDirectories(zipEntryName: String, baseFolderPath: String) {
        var subPath = ""

        for (i in zipEntryName.indices) {
            if (zipEntryName[i] != '/') subPath += zipEntryName[i]
            else {
                File(baseFolderPath + File.separator + subPath).mkdir()
                subPath += '/'
            }
        }
    }
}