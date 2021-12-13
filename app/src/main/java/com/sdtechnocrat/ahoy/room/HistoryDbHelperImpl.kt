package com.sdtechnocrat.ahoy.room

import com.sdtechnocrat.ahoy.data.HistoryItem

class HistoryDbHelperImpl(private val historyDb: HistoryDatabase): HistoryDbHelper {

    override suspend fun getSearchHistory(): List<HistoryItem> {
        return historyDb.historyDao().getAll()
    }

    override suspend fun insert(historyItem: HistoryItem) {
        return historyDb.historyDao().insert(historyItem)
    }

    override suspend fun update(historyItem: HistoryItem) {
        return historyDb.historyDao().update(historyItem)
    }

    override suspend fun delete(historyItem: HistoryItem) {
        return historyDb.historyDao().delete(historyItem)
    }
}