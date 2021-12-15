package com.sdtechnocrat.ahoy.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.sdtechnocrat.ahoy.adapters.ContentGridAdapter
import com.sdtechnocrat.ahoy.api.OkHttpBuilder.getOkHttpClient
import com.sdtechnocrat.ahoy.data.ContentItem
import com.sdtechnocrat.ahoy.databinding.ActivityCastDetailsBinding
import com.sdtechnocrat.ahoy.utilities.Util.Companion.AUTH_TOKEN
import com.sdtechnocrat.ahoy.utilities.Util.Companion.BASE_URL
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class CastDetailsActivity : AppCompatActivity() {

    private lateinit var binding : ActivityCastDetailsBinding

    private lateinit var cast_permalink : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCastDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        cast_permalink = intent.getStringExtra("cast_permalink").toString()

        getCastDetails()
    }

    private fun getCastDetails() {
        binding.progressIndicator.visibility = View.VISIBLE
        val urlBuilder : HttpUrl.Builder? = HttpUrl.parse(BASE_URL.plus("getCastDetail"))?.newBuilder()
        urlBuilder?.addQueryParameter("authToken", AUTH_TOKEN)
        urlBuilder?.addQueryParameter("permalink", cast_permalink)

        val url = urlBuilder?.build().toString()
        val request = Request.Builder().url(url).build()
        getOkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                TODO("Not yet implemented")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful && response.body() != null) {
                    runOnUiThread {
                        binding.progressIndicator.visibility = View.GONE
                        handleCastResponse(response.body()?.string() ?: "")
                    }
                }
            }
        })
    }

    private fun handleCastResponse(response: String) {
        val responseObj = JSONObject(response)
        val status = responseObj.optInt("status", 0)
        if (status == 200) {
            val name = responseObj.optString("name", "")
            val summary = responseObj.optString("summary", "")
            val castImage = responseObj.optString("cast_image", "")
            binding.textViewName.text = name
            binding.textViewBio.text = summary
            Glide.with(this).load(castImage).into(binding.imageViewCast)

            //Parsing Cast
            val moviesArray = responseObj.optJSONArray("movieList")
            if (moviesArray != null && moviesArray.length() > 0) {
                val moviesList = mutableListOf<ContentItem>()
                for (i in 0 until moviesArray.length()) {
                    val castObj = moviesArray.getJSONObject(i)
                    val permalink = castObj.optString("permalink", "")
                    val title = castObj.optString("name", "")
                    val poster = castObj.optString("posterForTv", "")
                    moviesList.add(ContentItem(permalink, title, poster))
                }

                val layoutManager = GridLayoutManager(this, 2)
                val adapter = ContentGridAdapter(this, moviesList, object :
                    ContentGridAdapter.OnItemClickListener {
                    override fun onItemClicked(contentItem: ContentItem) {
                        val intent = Intent(this@CastDetailsActivity, ContentDetailsActivity::class.java).apply {
                            putExtra("content_permalink", contentItem.permalink)
                            putExtra("content_title", contentItem.title)
                            putExtra("content_poster", contentItem.poster)
                        }
                        startActivity(intent)
                    }
                })
                binding.moviesRecycler.layoutManager = layoutManager
                binding.moviesRecycler.adapter = adapter
            }

        }
    }
}