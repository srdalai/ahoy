package com.sdtechnocrat.ahoy.api

import com.sdtechnocrat.ahoy.data.ContentDetailsResponse
import com.sdtechnocrat.ahoy.data.FeaturedContentResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface MuviApiService {

    @GET("getAppHomeFeature")
    fun getAppHomeFeature(@Query("authToken") authToken: String,
                          @Query("feature_sec_offset") feature_sec_offset: String,
                          @Query("feature_sec_limit") feature_sec_limit: String,
                          @Query("feature_content_offset") feature_content_offset: String,
                          @Query("feature_content_limit") feature_content_limit: String,
                          @Query("platform") platform: String
    ) : Call<FeaturedContentResponse>

    @GET("getContentDetails")
    fun getContentDetails(@Query("authToken") authToken : String,
                          @Query("permalink") permalink : String
    ) : Call<ContentDetailsResponse>

    @GET("RelatedContent")
    fun getRelatedContents(@Query("authToken") authToken : String,
                          @Query("content_id") content_id : String
    ) : Call<ContentDetailsResponse>
}