package com.sdtechnocrat.ahoy.utilities

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.sdtechnocrat.ahoy.api.OkHttpBuilder.getOkHttpClient
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.HttpURLConnection.HTTP_MULT_CHOICE
import java.net.HttpURLConnection.HTTP_OK

class DownloadWorker(appContext: Context, workerParams: WorkerParameters): Worker(appContext, workerParams)  {

    lateinit var okHttpClient: OkHttpClient

    var url = "https://d1ea1ipphuw1ti.cloudfront.net/16605/EncodedVideo/uploads/movie_stream/full_movie/705636/Lut_Gaye__Full_Song__Emraan_Hashmi__Yukti__Jubin_N__Tanishk_B__Manoj_M__Bhushan_K__Radhika_Vinay_144.mp4?Expires=1638422380&Signature=Q1Tqya9ZLCB7eLDDiuTg5CB7e1GEOXdLEg-8Z~Uk2xP8qnZwQnaBY91fTbvWHxPvFAsBBf2WR66tmnB5FEc38yjX3sPBTamw7eqFag0kQbao96SW5fKmMWfvtcQIK3ojboBD4zy3njIvDVvXz6V0Llv-OGVcag0cOkDmBmoCemz-5gRH5J0-iTsK23Jsdp7YbV3UhxQP20jcVpaNGEWKhLvRDUdSyfdeEHUHuZsEGg4uE60Chs6uWE-voX~XUo~r2tBjegyZwnHYYlzAIm2kXmBFeixBc7sPVdR-f6Y3Br4YKBFh8A3qV9DHiYVnXNIfxDwR0HyTTgovuevb5qeuCg__&Key-Pair-Id=APKAI7LE5J4L2WM2V57A"

    override fun doWork(): Result {
        downloadFile()
        return Result.success()
    }

    private fun downloadFile() {
        val request = Request.Builder().url(url).build()
        val response = getOkHttpClient().newCall(request).execute()
        val body = response.body()
        val responseCode = response.code()
        if (responseCode >= HTTP_OK && responseCode < HTTP_MULT_CHOICE && body != null) {
            //Read the file
            body.byteStream().apply {

            }
        } else {
            //Report Error
        }

    }
}