package com.sdtechnocrat.ahoy.data

import com.google.gson.annotations.SerializedName

data class FeaturedContentResponse(

    @field:SerializedName("msg")
    val msg: String? = null,

    @field:SerializedName("code")
    val code: Int? = null,

    @field:SerializedName("section_name")
    val featuredSections: List<FeaturedSectionItem?>? = null,

    @field:SerializedName("banner_text")
    val bannerText: String? = null,

    @field:SerializedName("banner_section_list")
    val bannerList: List<BannerItem?>? = null,

    @field:SerializedName("section_count")
    val sectionCount: Int? = null,

    @field:SerializedName("is_featured")
    val isFeatured: Int? = null,

    @field:SerializedName("status")
    val status: String? = null
)

data class FeaturedSectionItem(

    @field:SerializedName("section_type")
    val sectionType: String? = null,

    @field:SerializedName("total")
    val total: String? = null,

    @field:SerializedName("section_id")
    val sectionId: String? = null,

    @field:SerializedName("data")
    val sectionContents: List<FeaturedContentItem?>? = null,

    @field:SerializedName("title")
    val title: String? = null
)

data class BannerItem(

    @field:SerializedName("banner_permalink")
    val bannerPermalink: String? = null,

    @field:SerializedName("video_on_banner")
    val videoOnBanner: String? = null,

    @field:SerializedName("button_type")
    val buttonType: Int? = null,

    @field:SerializedName("web_link")
    val webLink: String? = null,

    @field:SerializedName("title")
    val title: String? = null,

    @field:SerializedName("text_heading")
    val textHeading: String? = null,

    @field:SerializedName("banner_type")
    val bannerType: String? = null,

    @field:SerializedName("image_path")
    val imagePath: String? = null,

    @field:SerializedName("banner_text")
    val bannerText: String? = null,

    @field:SerializedName("banner_url")
    val bannerUrl: String? = null,

    @field:SerializedName("content_types_id")
    val contentTypesId: String? = null,

    @field:SerializedName("feed_url")
    val feedUrl: String? = null,

    @field:SerializedName("button_text")
    val buttonText: String? = null,

    @field:SerializedName("other_sub_type")
    val otherSubType: String? = null
)

data class FeaturedContentItem(

    @field:SerializedName("season_no")
    val seasonNo: String? = null,

    @field:SerializedName("video_duration")
    val videoDuration: String? = null,

    @field:SerializedName("list_id")
    val listId: String? = null,

    @field:SerializedName("posterForTv")
    val posterForTv: String? = null,

    @field:SerializedName("rating")
    val rating: Float? = null,

    @field:SerializedName("episode_no")
    val episodeNo: String? = null,

    @field:SerializedName("tv_banner")
    val tvBanner: String? = null,

    @field:SerializedName("parent_poster_for_mobile_apps")
    val parentPosterForMobileApps: String? = null,

    @field:SerializedName("censor_rating")
    val censorRating: String? = null,

    @field:SerializedName("is_media_published")
    val isMediaPublished: Int? = null,

    /*@field:SerializedName("genre")
    val genre: String? = null,*/

    @field:SerializedName("is_play_list")
    val isPlayList: String? = null,

    @field:SerializedName("preview_video_url")
    val previewVideoUrl: String? = null,

    @field:SerializedName("content_types_id")
    val contentTypesId: String? = null,

    @field:SerializedName("parent_title")
    val parentTitle: String? = null,

    @field:SerializedName("poster_url")
    val posterUrl: String? = null,

    @field:SerializedName("banner")
    val banner: String? = null,

    @field:SerializedName("isFreeContent")
    val isFreeContent: Int? = null,

    @field:SerializedName("play_list_type")
    val playListType: String? = null,

    @field:SerializedName("release_date")
    val releaseDate: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("is_episode")
    val isEpisode: String? = null,

    @field:SerializedName("is_livestream_enabled")
    val isLivestreamEnabled: Int? = null,

    @field:SerializedName("total_season")
    val totalSeason: Int? = null,

    @field:SerializedName("muvi_uniq_id")
    val muviUniqId: String? = null,

    @field:SerializedName("movie_stream_uniq_id")
    val movieStreamUniqId: String? = null,

    @field:SerializedName("permalink")
    val permalink: String? = null,

    @field:SerializedName("is_converted")
    val isConverted: String? = null,

    @field:SerializedName("story")
    val story: String? = null
)
