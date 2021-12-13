package com.sdtechnocrat.ahoy.room

import com.sdtechnocrat.ahoy.data.HistoryItem

interface HistoryDbHelper {

    suspend fun getSearchHistory(): List<HistoryItem>

    suspend fun insert(historyItem: HistoryItem)

    suspend fun update(historyItem: HistoryItem)

    suspend fun delete(historyItem: HistoryItem)
}