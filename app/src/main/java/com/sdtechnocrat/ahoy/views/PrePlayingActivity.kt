package com.sdtechnocrat.ahoy.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.sdtechnocrat.ahoy.R
import com.sdtechnocrat.ahoy.api.OkHttpBuilder.getOkHttpClient
import com.sdtechnocrat.ahoy.databinding.ActivityPrePlayingBinding
import com.sdtechnocrat.ahoy.utilities.Util.Companion.AUTH_TOKEN
import com.sdtechnocrat.ahoy.utilities.Util.Companion.BASE_URL
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class PrePlayingActivity : AppCompatActivity() {

    lateinit var binding : ActivityPrePlayingBinding

    lateinit var contentPermalink : String
    lateinit var movieUniqueId : String
    lateinit var streamUniqueId : String
    lateinit var videoUrl : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrePlayingBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        movieUniqueId = intent.getStringExtra("muvi_unique_id").toString()
        streamUniqueId = intent.getStringExtra("stream_unique_id").toString()

        getVideoDetails()
    }

    private fun getVideoDetails() {
        binding.progressIndicator.visibility = View.VISIBLE
        val urlBuilder : HttpUrl.Builder? = HttpUrl.parse(BASE_URL.plus("getVideoDetails"))?.newBuilder()
        urlBuilder?.addQueryParameter("authToken", AUTH_TOKEN)
        urlBuilder?.addQueryParameter("content_uniq_id", movieUniqueId)
        urlBuilder?.addQueryParameter("stream_uniq_id", streamUniqueId)
        val url = urlBuilder?.build().toString()
        val request = Request.Builder().url(url).build()
        getOkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                TODO("Not yet implemented")
            }

            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    binding.progressIndicator.visibility = View.GONE
                    if (response.isSuccessful && response.body() != null) {
                        handleVideoDetailsResponse(response.body()!!.string())
                    }
                }
            }

        })
    }

    private fun handleVideoDetailsResponse(responseStr: String) {
        val responseObj = JSONObject(responseStr)
        val code = responseObj.optInt("code", 0)
        if (code == 200) {
            videoUrl = responseObj.optString("videoUrl")

            startPlayer()
        }
    }

    private fun startPlayer() {
        val intent = Intent(this, PlayerActivity::class.java).apply {
            putExtra("video_url", videoUrl)
        }
        startActivity(intent)
        finishAfterTransition()
    }
}