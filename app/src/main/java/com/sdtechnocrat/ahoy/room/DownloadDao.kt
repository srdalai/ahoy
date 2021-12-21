package com.sdtechnocrat.ahoy.room

import androidx.room.*
import com.sdtechnocrat.ahoy.data.DownloadItem

@Dao
interface DownloadDao {

    @Query("SELECT * from downloaditem ORDER BY update_time DESC")
    fun getAll(): List<DownloadItem>

    @Query("SELECT * from downloaditem WHERE stream_id=:streamId")
    fun getDownloadById(streamId: String): List<DownloadItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: DownloadItem)

    @Update
    fun update(item: DownloadItem)

    @Delete
    fun delete(item: DownloadItem)

    @Query("DELETE from downloaditem")
    fun deleteAll()
}