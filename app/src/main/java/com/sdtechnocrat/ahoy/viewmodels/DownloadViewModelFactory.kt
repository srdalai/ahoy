package com.sdtechnocrat.ahoy.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sdtechnocrat.ahoy.room.DownloadDbHelper

class DownloadViewModelFactory(private val dbHelper: DownloadDbHelper): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DownloadsFragmentViewModel::class.java)) {
            return DownloadsFragmentViewModel(dbHelper) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}