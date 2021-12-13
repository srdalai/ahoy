package com.sdtechnocrat.ahoy.api

import com.localebro.okhttpprofiler.OkHttpProfilerInterceptor
import okhttp3.OkHttpClient

object OkHttpBuilder {

    private val builder = OkHttpClient.Builder()
        .addInterceptor(OkHttpProfilerInterceptor())

    private val client = builder.build()

    fun getOkHttpClient() : OkHttpClient {
        return client
    }
}