package com.sdtechnocrat.ahoy.data

data class ContentItem(
    val permalink : String,
    val title: String,
    val poster: String,
    var contentTypesId: Int,
    var contentId : String,
    var contentStreamId : String
) {
    constructor(permalink : String, title: String, poster: String) : this(permalink, title, poster, 1, "", "")
}
