package com.sdtechnocrat.ahoy.room

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.sdtechnocrat.ahoy.data.HistoryItem

@Dao
interface HistoryDao {

    @Query("SELECT * FROM historyitem ORDER BY update_time DESC")
    fun getAll(): List<HistoryItem>

    @Insert(onConflict = REPLACE)
    fun insert(terms: HistoryItem)

    @Update
    fun update(term: HistoryItem)

    @Delete
    fun delete(term: HistoryItem)
}