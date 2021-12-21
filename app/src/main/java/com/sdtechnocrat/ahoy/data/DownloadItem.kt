package com.sdtechnocrat.ahoy.data

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.jetbrains.annotations.NotNull

@Entity
data class DownloadItem(
    @PrimaryKey
    @ColumnInfo(name = "stream_id")
    @NonNull
    var streamId : String = "",

    @ColumnInfo(name = "movie_id")
    @NonNull
    var movieId : String = "",

    @ColumnInfo(name = "content_name")
    var contentName : String = "",

    @ColumnInfo(name = "content_poster")
    var contentPoster : String = "",

    @ColumnInfo(name = "video_url")
    var videoUrl : String = "",

    @ColumnInfo(name = "file_uri")
    var fileUri : String = "",

    @ColumnInfo(name = "file_size")
    var fileSize : String = "",

    @ColumnInfo(name = "downloaded_size")
    var downloadedSize : String = "",

    @ColumnInfo(name = "update_time")
    @NotNull
    var updateTime: Long = 0
)
