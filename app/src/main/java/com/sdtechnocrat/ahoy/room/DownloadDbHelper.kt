package com.sdtechnocrat.ahoy.room

import com.sdtechnocrat.ahoy.data.DownloadItem

interface DownloadDbHelper {

    suspend fun getDownloads(): List<DownloadItem>

    suspend fun getDownloadById(streamId: String): List<DownloadItem>

    suspend fun insert(downloadItem: DownloadItem)

    suspend fun update(downloadItem: DownloadItem)

    suspend fun delete(downloadItem: DownloadItem)

    suspend fun deleteAll()
}