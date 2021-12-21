package com.sdtechnocrat.ahoy.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sdtechnocrat.ahoy.room.HistoryDbHelper

class ViewModelFactory(private val dbHelper: HistoryDbHelper): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchFragmentViewModel::class.java)) {
            return SearchFragmentViewModel(dbHelper) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}