package com.erman.usurf.home.model

import android.os.Build
import android.os.StatFs
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import com.erman.usurf.application.MainApplication.Companion.appContext
import com.erman.usurf.R
import com.erman.usurf.databinding.StorageButtonBinding
import com.erman.usurf.utils.StoragePaths
import kotlinx.android.synthetic.main.storage_button.view.*

class HomeModel() {
    fun createStorageButtons(): MutableList<StorageButtonBinding> {
        val storageButtons: MutableList<StorageButtonBinding> = mutableListOf<StorageButtonBinding>()
        val storageDirectories = StoragePaths().getStorageDirectories()

        for (i in storageDirectories.indices) {
            val binding: StorageButtonBinding = DataBindingUtil.inflate(LayoutInflater.from(appContext), R.layout.storage_button, null, false)
            storageButtons.add(binding)
            storageButtons[i].root.tag = storageDirectories.elementAt(i)
            storageButtons[i].root.buttonText.text =storageDirectories.elementAt(i)
            storageButtons[i].root.progressBar?.scaleY=20f
            //It is null on older versions of android because I removed it from the layout
        }
        return storageButtons
    }

    private fun getTotalStorage(path: String): Long {
        val stat = StatFs(path)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            return stat.totalBytes
        }
        return 0
    }

    private fun getUsedStorage(path: String): Long {
        val stat = StatFs(path)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            val free = stat.availableBlocksLong
            val blockSize = stat.blockSizeLong
            val total = stat.totalBytes
            return total - (free * blockSize)
        }
        return 0
    }

    fun getUsedStoragePercentage(path: String): Int {
        if (path != "/" && (getTotalStorage(path)).toInt() != 0) return ((getUsedStorage(
            path
        ) * 100 / getTotalStorage(path))).toInt()
        return 0
    }
}