package com.sdtechnocrat.ahoy.data

import com.google.gson.annotations.SerializedName

data class ContentDetailsResponse(

    @field:SerializedName("msg")
    val msg: String? = null,


    @field:SerializedName("code")
    val code: Int? = null,

    /*@field:SerializedName("epDetails")
    val epDetails: EpDetails? = null,*/

    @field:SerializedName("category_name")
    val categoryName: String? = null,

    @field:SerializedName("movie")
    val movie: Movie? = null,

    @field:SerializedName("rating")
    val rating: String? = null,

    @field:SerializedName("is_download_available")
    val isDownloadAvailable: Int? = null,

    @field:SerializedName("isFollowed")
    val isFollowed: Int? = null,

    @field:SerializedName("preorder_success_msg")
    val preorderSuccessMsg: String? = null,

    @field:SerializedName("review")
    val review: String? = null,

    @field:SerializedName("content_category_id")
    val contentCategoryId: String? = null,
)

data class EpDetails(

    @field:SerializedName("series_number")
    val seriesNumber: String? = null,

    @field:SerializedName("total_series")
    val totalSeries: String? = null
)


data class Movie(

    @field:SerializedName("name")
    val name: String? = "",

    @field:SerializedName("genre")
    val genre: String? = "",

    @field:SerializedName("story")
    val story: String? = "",

    @field:SerializedName("posterForTv")
    val posterForTv: String? = "",

    @field:SerializedName("release_date")
    val releaseDate: String? = "",

    @field:SerializedName("video_duration")
    val videoDuration: String? = "",

    @field:SerializedName("cast_detail")
    val castDetails: List<CastDetailItem>? = null,

)

data class CastDetailItem(

    @field:SerializedName("celeb_name")
    val celebName: String? = null,

    @field:SerializedName("celeb_id")
    val celebId: String? = null,

    @field:SerializedName("cast_type")
    val castType: String? = null,

    @field:SerializedName("celeb_image")
    val celebImage: String? = null,

    @field:SerializedName("permalink")
    val permalink: String? = null
)
