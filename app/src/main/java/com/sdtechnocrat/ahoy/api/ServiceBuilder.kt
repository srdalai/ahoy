package com.sdtechnocrat.ahoy.api

import com.localebro.okhttpprofiler.OkHttpProfilerInterceptor
import com.sdtechnocrat.ahoy.utilities.Util.Companion.BASE_URL
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceBuilder {

    private val builder = OkHttpClient.Builder()
        .addInterceptor(OkHttpProfilerInterceptor())

    private val client = builder.build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    fun<T> buildService(service: Class<T>): T{
        return retrofit.create(service)
    }
}