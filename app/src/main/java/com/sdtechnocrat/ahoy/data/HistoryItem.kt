package com.sdtechnocrat.ahoy.data

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class HistoryItem(

    @PrimaryKey
    @ColumnInfo(name = "search_term")
    @NonNull
    var searchTerm : String,

    @ColumnInfo(name = "update_time")
    var updateTime: Long
)
