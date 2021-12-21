package com.sdtechnocrat.ahoy.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sdtechnocrat.ahoy.data.DownloadItem
import com.sdtechnocrat.ahoy.data.HistoryItem
import com.sdtechnocrat.ahoy.room.DownloadDbHelper
import com.sdtechnocrat.ahoy.utilities.Util
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DownloadsFragmentViewModel(private val dbHelper: DownloadDbHelper): ViewModel() {

    private val TAG = "DownloadsFragViewModel"

    private val downloadItems = MutableLiveData<List<DownloadItem>>()

    init {
        fetchDownloads()
    }

    private fun fetchDownloads() {
        GlobalScope.launch {
            try {
                val downloadsDb = dbHelper.getDownloads()
                downloadItems.postValue(downloadsDb)
            } catch (e: Exception) {
                Log.e(TAG, "DB Fetch Exception ->\n" + e)
            }
        }
    }

    fun insertDownload(downloadItem: DownloadItem) {
        GlobalScope.launch {
            try {
                val timestamp = System.currentTimeMillis()
                downloadItem.updateTime = timestamp
                dbHelper.insert(downloadItem)
            } catch (e: Exception) {
                Log.e(TAG, "DB Insert Exception ->\n" + e)
            }
        }
    }

    fun delete(downloadItem: DownloadItem) {
        GlobalScope.launch {
            try {
                dbHelper.delete(downloadItem)
                fetchDownloads()
            } catch (e: Exception) {
                Log.e(TAG, "DB Delete Exception ->\n" + e)
            }
        }
    }

    fun deleteAll() {
        GlobalScope.launch {
            try {
                dbHelper.deleteAll()
            } catch (e: Exception) {
                Log.e(TAG, "DB Delete All Exception ->\n" + e)
            }
        }
    }

    fun getDownloadItems(): LiveData<List<DownloadItem>> {
        return downloadItems
    }
}