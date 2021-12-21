package com.sdtechnocrat.ahoy.room

import com.sdtechnocrat.ahoy.data.DownloadItem

class DownloadDbHelperImpl(private val downloadDb: DownloadDatabase): DownloadDbHelper {

    override suspend fun getDownloads(): List<DownloadItem> {
        return downloadDb.downloadDao().getAll()
    }

    override suspend fun getDownloadById(streamId: String): List<DownloadItem> {
        return downloadDb.downloadDao().getDownloadById(streamId)
    }

    override suspend fun insert(downloadItem: DownloadItem) {
        return downloadDb.downloadDao().insert(downloadItem)
    }

    override suspend fun update(downloadItem: DownloadItem) {
        return downloadDb.downloadDao().update(downloadItem)
    }

    override suspend fun delete(downloadItem: DownloadItem) {
        return downloadDb.downloadDao().delete(downloadItem)
    }

    override suspend fun deleteAll() {
        return downloadDb.downloadDao().deleteAll()
    }
}