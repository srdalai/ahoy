package com.sdtechnocrat.ahoy.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.sdtechnocrat.ahoy.R
import com.sdtechnocrat.ahoy.adapters.ContentGridAdapter
import com.sdtechnocrat.ahoy.api.OkHttpBuilder.getOkHttpClient
import com.sdtechnocrat.ahoy.data.ContentItem
import com.sdtechnocrat.ahoy.databinding.ActivityContentListingBinding
import com.sdtechnocrat.ahoy.utilities.SharedPref
import com.sdtechnocrat.ahoy.utilities.Util.Companion.AUTH_TOKEN
import com.sdtechnocrat.ahoy.utilities.Util.Companion.BASE_URL
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class ContentListingActivity : AppCompatActivity() {

    lateinit var binding: ActivityContentListingBinding
    lateinit var sharedPref: SharedPref

    val contentList = mutableListOf<ContentItem>()
    lateinit var adapter: ContentGridAdapter

    lateinit var userId: String
    lateinit var permalink : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityContentListingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPref = SharedPref.getInstance(this)

        userId = sharedPref.getUserId().toString()

        adapter = ContentGridAdapter(this, contentList, object :
            ContentGridAdapter.OnItemClickListener {
            override fun onItemClicked(contentItem: ContentItem) {
                val intent = Intent(this@ContentListingActivity, ContentDetailsActivity::class.java).apply {
                    putExtra("content_permalink", contentItem.permalink)
                    putExtra("content_title", contentItem.title)
                    putExtra("content_poster", contentItem.poster)
                }
                startActivity(intent)
            }
        })
        binding.contentRecycler.layoutManager = GridLayoutManager(this, 2)
        binding.contentRecycler.adapter = adapter

        val action = intent.getStringExtra("action")
        when(action) {
            ACTION_WATCH_LIST -> {
                binding.textViewTitle.text = "Watch List"
                getFavorites()
            }
            ACTION_LIBRARY -> {
                binding.textViewTitle.text = "Purchased Contents"
                getMyLibrary()
            }
            ACTION_WATCH_HISTORY -> {
                binding.textViewTitle.text = "Watch History"
                getWatchHistory()
            }
            ACTION_CONTENT_LIST -> {
                permalink = intent.getStringExtra("permalink").toString()
                val title = intent.getStringExtra("name").toString()
                binding.textViewTitle.text = title
                //getContentList()
            }
        }
    }

    private fun getFavorites() {
        binding.progressIndicator.visibility = View.VISIBLE
        val urlBuilder = HttpUrl.parse(BASE_URL.plus("ViewFavourite"))?.newBuilder()
        urlBuilder?.addQueryParameter("authToken", AUTH_TOKEN)
        urlBuilder?.addQueryParameter("user_id", userId)
        val url = urlBuilder?.build().toString()
        val request = Request.Builder().url(url).build()

        getOkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                TODO("Not yet implemented")
            }

            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    binding.progressIndicator.visibility = View.GONE
                    if (response.isSuccessful) {
                        handleFavoriteResponse(response.body()?.string()!!)
                    }
                }
            }

        })
    }

    private fun handleFavoriteResponse(responseStr: String) {
        val responseObj = JSONObject(responseStr)
        val status = responseObj.optInt("status", 0)
        val message = responseObj.optString("msg", "Error")
        if (status == 200) {
            val movieArray = responseObj.getJSONArray("movieList")
            if (movieArray.length() > 0) {
                for (i in 0 until movieArray.length()) {
                    val movieObj = movieArray.getJSONObject(i)
                    val permalink = movieObj.optString("permalink", "")
                    val title = movieObj.optString("title", "")
                    val poster = movieObj.optString("posterForTv", "")
                    contentList.add(ContentItem(permalink, title, poster))
                }

                adapter.notifyItemInserted(0)
                binding.contentRecycler.visibility = View.VISIBLE
            }
        }
    }

    private fun getMyLibrary() {
        binding.progressIndicator.visibility = View.VISIBLE
        val urlBuilder = HttpUrl.parse(BASE_URL.plus("MyLibrary"))?.newBuilder()
        urlBuilder?.addQueryParameter("authToken", AUTH_TOKEN)
        urlBuilder?.addQueryParameter("user_id", userId)
        val url = urlBuilder?.build().toString()
        val request = Request.Builder().url(url).build()

        getOkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                TODO("Not yet implemented")
            }

            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    binding.progressIndicator.visibility = View.GONE
                    if (response.isSuccessful) {
                        handleLibraryResponse(response.body()?.string()!!)
                    }
                }
            }

        })
    }

    private fun handleLibraryResponse(responseStr: String) {
        val responseObj = JSONObject(responseStr)
        val code = responseObj.optInt("code", 0)
        val message = responseObj.optString("msg", "Error")
        if (code == 200) {
            val movieArray = responseObj.getJSONArray("mylibrary")
            if (movieArray.length() > 0) {
                for (i in 0 until movieArray.length()) {
                    val movieObj = movieArray.getJSONObject(i)
                    val permalink = movieObj.optString("permalink", "")
                    val name = movieObj.optString("name", "")
                    val poster = movieObj.optString("posterForTv", "")
                    contentList.add(ContentItem(permalink, name, poster))
                }

                adapter.notifyItemInserted(0)
                binding.contentRecycler.visibility = View.VISIBLE
            }
        }
    }

    private fun getWatchHistory() {
        binding.progressIndicator.visibility = View.VISIBLE
        val urlBuilder = HttpUrl.parse(BASE_URL.plus("watchHistory"))?.newBuilder()
        urlBuilder?.addQueryParameter("authToken", AUTH_TOKEN)
        urlBuilder?.addQueryParameter("user_id", userId)
        val url = urlBuilder?.build().toString()
        val request = Request.Builder().url(url).build()

        getOkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                TODO("Not yet implemented")
            }

            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    binding.progressIndicator.visibility = View.GONE
                    if (response.isSuccessful) {
                        handleWatchHistoryResponse(response.body()?.string()!!)
                    }
                }
            }

        })
    }

    private fun handleWatchHistoryResponse(responseStr: String) {
        val responseObj = JSONObject(responseStr)
        val code = responseObj.optInt("code", 0)
        val message = responseObj.optString("msg", "Error")
        if (code == 200) {
            val movieArray = responseObj.getJSONArray("list")
            if (movieArray.length() > 0) {
                for (i in 0 until movieArray.length()) {
                    val movieObj = movieArray.getJSONObject(i)
                    val permalink = movieObj.optString("permalink", "")
                    val title = movieObj.optString("title", "")
                    val poster = movieObj.optString("posterForTv", "")
                    contentList.add(ContentItem(permalink, title, poster))
                }

                adapter.notifyItemInserted(0)
                binding.contentRecycler.visibility = View.VISIBLE
            }
        }
    }

    private fun getContentList() {
        binding.progressIndicator.visibility = View.VISIBLE
        val urlBuilder = HttpUrl.parse(BASE_URL.plus("getContentList"))?.newBuilder()
        urlBuilder?.addQueryParameter("authToken", AUTH_TOKEN)
        urlBuilder?.addQueryParameter("permalink", permalink)
        val url = urlBuilder?.build().toString()
        val request = Request.Builder().url(url).build()

        getOkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                TODO("Not yet implemented")
            }

            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    binding.progressIndicator.visibility = View.GONE
                    if (response.isSuccessful) {
                        handleContentListResponse(response.body()?.string()!!)
                    }
                }
            }

        })
    }

    private fun handleContentListResponse(responseStr: String) {
        val responseObj = JSONObject(responseStr)
        val code = responseObj.optInt("code", 0)
        val message = responseObj.optString("msg", "Error")
        if (code == 200) {
            val movieArray = responseObj.getJSONArray("list")
            if (movieArray.length() > 0) {
                for (i in 0 until movieArray.length()) {
                    val movieObj = movieArray.getJSONObject(i)
                    val permalink = movieObj.optString("permalink", "")
                    val title = movieObj.optString("title", "")
                    val poster = movieObj.optString("posterForTv", "")
                    contentList.add(ContentItem(permalink, title, poster))
                }

                adapter.notifyItemInserted(0)
                binding.contentRecycler.visibility = View.VISIBLE
            }
        }
    }

    companion object {
        const val ACTION_WATCH_LIST = "watch-list"
        const val ACTION_LIBRARY = "my-library"
        const val ACTION_WATCH_HISTORY = "watch-history"
        const val ACTION_CONTENT_LIST = "content_list"
    }
}