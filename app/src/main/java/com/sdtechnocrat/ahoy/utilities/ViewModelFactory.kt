package com.sdtechnocrat.ahoy.utilities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sdtechnocrat.ahoy.room.HistoryDbHelper
import com.sdtechnocrat.ahoy.viewmodels.SearchFragmentViewModel

class ViewModelFactory(private val dbHelper: HistoryDbHelper): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchFragmentViewModel::class.java)) {
            return SearchFragmentViewModel(dbHelper) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}