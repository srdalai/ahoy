package com.sdtechnocrat.ahoy.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sdtechnocrat.ahoy.data.HistoryItem
import com.sdtechnocrat.ahoy.room.HistoryDbHelper
import com.sdtechnocrat.ahoy.utilities.Util.Companion.SEARCH_HISTORY_LIMIT
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Exception

class SearchFragmentViewModel(private val dbHelper: HistoryDbHelper) : ViewModel() {

    private val TAG = "SearchFragmentViewModel"

    private val historyItems = MutableLiveData<List<HistoryItem>?>()

    init {
        fetchHistory()
    }

    private fun fetchHistory() {
        GlobalScope.launch {
            try {
                val historyFromDb = dbHelper.getSearchHistory()
                historyItems.postValue(historyFromDb)
            } catch (e: Exception) {
                Log.e(TAG, "DB Fetch Exception ->\n" + e)
            }
        }
    }

    fun insertHistory(searchTerm: String) {
        GlobalScope.launch {
            try {
                val historyFromDb = dbHelper.getSearchHistory()
                if (historyFromDb.size >= SEARCH_HISTORY_LIMIT) {   //If history is more than limit, remove last item
                    dbHelper.delete(historyFromDb[historyFromDb.size-1])
                }
                val timestamp = System.currentTimeMillis()
                val historyItem = HistoryItem(searchTerm, timestamp)
                dbHelper.insert(historyItem)
                fetchHistory()
            } catch (e: Exception) {
                Log.e(TAG, "DB Insert Exception ->\n" + e)
            }
        }
    }

    fun updateHistory(historyItem: HistoryItem) {
        GlobalScope.launch {
            try {
                val timestamp = System.currentTimeMillis()
                historyItem.updateTime = timestamp
                dbHelper.update(historyItem)
                fetchHistory()
            } catch (e: Exception) {
                Log.e(TAG, "DB Update Exception ->\n" + e)
            }
        }
    }

    fun getHistory(): LiveData<List<HistoryItem>?> {
        return historyItems
    }
}