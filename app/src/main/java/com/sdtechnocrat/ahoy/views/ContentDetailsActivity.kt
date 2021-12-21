package com.sdtechnocrat.ahoy.views

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.mediarouter.app.MediaRouteButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.cast.MediaInfo
import com.google.android.gms.cast.MediaLoadRequestData
import com.google.android.gms.cast.MediaMetadata
import com.google.android.gms.cast.framework.*
import com.google.android.gms.common.images.WebImage
import com.sdtechnocrat.ahoy.R
import com.sdtechnocrat.ahoy.adapters.CastAdapter
import com.sdtechnocrat.ahoy.api.OkHttpBuilder.getOkHttpClient
import com.sdtechnocrat.ahoy.data.CastDetailItem
import com.sdtechnocrat.ahoy.data.DownloadItem
import com.sdtechnocrat.ahoy.data.SeasonItem
import com.sdtechnocrat.ahoy.databinding.ActivityContentDetailsBinding
import com.sdtechnocrat.ahoy.room.DownloadDao
import com.sdtechnocrat.ahoy.room.DownloadDatabase
import com.sdtechnocrat.ahoy.utilities.SharedPref
import com.sdtechnocrat.ahoy.utilities.Util
import com.sdtechnocrat.ahoy.utilities.Util.Companion.AUTH_TOKEN
import com.sdtechnocrat.ahoy.utilities.Util.Companion.BASE_URL
import com.sdtechnocrat.ahoy.utilities.Util.Companion.formatVideoDuration
import com.sdtechnocrat.ahoy.views.fragments.EpisodesFragment
import com.sdtechnocrat.ahoy.views.fragments.SeasonFragment
import com.sdtechnocrat.ahoy.views.fragments.SimilarFragment
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.*
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.net.HttpURLConnection
import java.util.*


class ContentDetailsActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "ContentDetailsActivity"
        private const val BUFFER_LENGTH_BYTES = 1024 * 8
        private const val HTTP_TIMEOUT = 30
    }

    private lateinit var binding: ActivityContentDetailsBinding

    private lateinit var sharedPref: SharedPref
    private lateinit var castContext : CastContext
    lateinit var downloadDatabase: DownloadDatabase
    lateinit var downloadDao : DownloadDao

    lateinit var userId : String

    private lateinit var contentPermalink : String
    private lateinit var contentTypesId : String
    private lateinit var contentTitle : String
    private lateinit var contentDesc : String
    private lateinit var contentPoster : String
    private lateinit var contentId : String
    private lateinit var contentStreamId : String
    private lateinit var genreText : String
    private lateinit var movieUniqueId : String
    private lateinit var streamUniqueId : String
    private lateinit var censorRating : String
    private var isConverted : Int = 0
    private var videoDurationLong : Long = 0

    private lateinit var videoUrlForCast : String
    private lateinit var videoUrlForDownload : String

    private var isFavorite : Int = 0
    var selectedSeasonPos = 0
    private var isDownloading = false
    private var isDownloaded = false

    private val metaDataList : MutableList<String> = mutableListOf()
    private val castList : MutableList<CastDetailItem> = mutableListOf()
    private val seasonList : ArrayList<SeasonItem> = arrayListOf()

    private lateinit var castAdapter : CastAdapter

    private var mCastSession: CastSession? = null
    private lateinit var mSessionManager: SessionManager
    private val mSessionManagerListener: SessionManagerListener<CastSession> = SessionManagerListenerImpl()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContentDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        downloadDatabase = DownloadDatabase.getInstance(this@ContentDetailsActivity)
        downloadDao = downloadDatabase.downloadDao()
        sharedPref = SharedPref.getInstance(this)
        userId = sharedPref.getUserId().toString()

        initialize()
        setUpListeners()
        getContentDetails()
        setupCast()
    }

    private fun setupToolbar() {
        val toolbar : Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val imageViewTrailing : ImageView = findViewById(R.id.imageViewTrailing)
        imageViewTrailing.visibility = View.GONE
    }

    private fun initialize() {
        contentPermalink = intent.getStringExtra("content_permalink").toString()
        contentTitle = intent.getStringExtra("content_title").toString()
        contentPoster = intent.getStringExtra("content_poster").toString()

        binding.textViewContentTitle.text = contentTitle
        Glide.with(this).load(contentPoster).into(binding.imageViewPoster)

        binding.castRecycler.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        castAdapter = CastAdapter(this, castList, object :
            CastAdapter.OnItemClickListener {
            override fun onItemClicked(castDetailItem: CastDetailItem) {
                val intent = Intent(this@ContentDetailsActivity, CastDetailsActivity::class.java).apply {
                    putExtra("cast_permalink", castDetailItem.permalink)
                }
                startActivity(intent)
            }
        })
        binding.castRecycler.adapter = castAdapter
        binding.castRecycler.addItemDecoration(ItemDecoration())
    }

    private fun setUpListeners() {
        binding.btnTrailer.setOnClickListener {

        }

        binding.btnDownload.setOnClickListener {
            if (!isDownloading) {
                if (isDownloaded) {
                    val intent = Intent(this, CommonActivity::class.java).apply {
                        putExtra("action", CommonActivity.ACTION_DOWNLOADS)
                    }
                    startActivity(intent)
                } else {
                    downloadContent()
                }
            }
        }

        binding.btnWatchlist.setOnClickListener {
            if (isFavorite == 1) {
                removeFromFavorite()
            } else {
                addToFavorite()
            }
        }

        binding.btnShare.setOnClickListener {
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "I am watching \"$contentTitle\"on Ahoy")
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }

        binding.btnWatchNow.setOnClickListener {
            if (::mSessionManager.isInitialized && mSessionManager.currentCastSession?.isConnected == true) {
                castVideoToRemoteMediaClient()
            } else {
                val intent = Intent(this, PrePlayingActivity::class.java).apply {
                    putExtra("muvi_unique_id", movieUniqueId)
                    putExtra("stream_unique_id", streamUniqueId)
                }
                startActivity(intent)
            }
        }

        binding.textViewSeason.setOnClickListener {
            val seasonFragment = SeasonFragment()
            seasonFragment.arguments = Bundle().apply {
                putInt("selected_season_pos", selectedSeasonPos)
                putParcelableArrayList("seasons", seasonList)
            }
            seasonFragment.show(supportFragmentManager, SeasonFragment.TAG)
        }
    }

    private fun downloadContent() {
        isDownloading = true
        binding.btnDownload.setText("Downloading")
        binding.btnDownload.setIconDrawable(ContextCompat.getDrawable(this@ContentDetailsActivity, R.drawable.downloading))
        GlobalScope.launch {
            val downloadItem = DownloadItem()
            downloadItem.streamId = streamUniqueId
            downloadItem.movieId = movieUniqueId
            downloadItem.contentName = contentTitle
            downloadItem.contentPoster = contentPoster
            downloadItem.videoUrl = videoUrlForDownload
            downloadItem.updateTime = System.currentTimeMillis()
            downloadDao.insert(downloadItem)
            Log.d(TAG, "Inserted to Downloads DB")

            val uri = downloadFile()
            val fileSize: String
            val file = File(uri.toString())
            val fileSizeKb = (file.length() / 1024).toString().toInt()
            fileSize = if (fileSizeKb > 1024) {
                val fileSizeMb = (fileSizeKb / 1024).toString().toInt()
                "$fileSizeMb MB"
            } else {
                "$fileSizeKb KB"
            }
            /*fileSize = getStringSizeLengthFile(fileSizeKb)*/

            downloadItem.fileUri = uri.toString()
            downloadItem.fileSize = fileSize
            downloadItem.downloadedSize = fileSize
            downloadDao.insert(downloadItem)
            runOnUiThread {
                isDownloading = false
                isDownloaded = true
                Toast.makeText(this@ContentDetailsActivity, "Download Completed", Toast.LENGTH_LONG).show()
                binding.btnDownload.setText("Downloaded")
                binding.btnDownload.setIconDrawable(ContextCompat.getDrawable(this@ContentDetailsActivity, R.drawable.download_done))
            }
        }
    }

    /*fun getStringSizeLengthFile(size: Long): String {
        val df = DecimalFormat("0.00")
        val sizeKb = 1024.0f
        val sizeMb = sizeKb * sizeKb
        val sizeGb = sizeMb * sizeKb
        val sizeTerra = sizeGb * sizeKb
        if (size < sizeMb) return df.format(size / sizeKb)
            .toString() + " Kb" else if (size < sizeGb) return df.format(size / sizeMb)
            .toString() + " Mb" else if (size < sizeTerra) return df.format(size / sizeGb)
            .toString() + " Gb"
        return ""
    }*/

    private fun downloadFile(): String? {
        val okHttpClient: OkHttpClient = OkHttpClient.Builder().build()
        try {
            val fileName = streamUniqueId.plus("_").plus(movieUniqueId).plus("_video.mp4")
            var targetFile = File(getDir("downloads", Context.MODE_PRIVATE), fileName)
            if (targetFile.exists()) {
                targetFile.delete()
                targetFile = File(getDir("downloads", Context.MODE_PRIVATE), fileName)
            }

            val request = Request.Builder().url(videoUrlForDownload).build()
            val response = okHttpClient.newCall(request).execute()
            val body = response.body()
            val responseCode = response.code()
            if (responseCode >= HttpURLConnection.HTTP_OK && responseCode < HttpURLConnection.HTTP_MULT_CHOICE && body != null) {
                //Read the file
                val length = body.contentLength()
                body.byteStream().apply {
                    targetFile.outputStream().use { fileOut ->
                        run {
                            var bytesCopied = 0
                            val buffer = ByteArray(BUFFER_LENGTH_BYTES)
                            var bytes = read(buffer)
                            while (bytes >= 0) {
                                fileOut.write(buffer, 0, bytes)
                                bytesCopied += bytes
                                bytes = read(buffer)
                            }
                        }

                    }
                }
                Log.d(TAG, "Download Completed. Path ->\n" + targetFile.absoluteFile)
                return targetFile.absoluteFile.path
            } else {
                //Report Error
                return null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception -> $e")
            return null
        }
    }

    private fun setupCast() {
        castContext = CastContext.getSharedInstance(this)
        mSessionManager = castContext.sessionManager
        val mMediaRouteButton = findViewById<View>(R.id.media_route_button) as MediaRouteButton
        CastButtonFactory.setUpMediaRouteButton(applicationContext, mMediaRouteButton)

        if (mSessionManager.currentCastSession?.isConnected == true) {
            updateUI()
        } else {
            revertUI()
        }
    }

    /*private fun setupViewPager() {
        val contentItem = ContentItem(contentPermalink, contentTitle, contentPoster)
        contentItem.contentTypesId = contentTypesId.toInt()
        contentItem.contentId = contentId
        contentItem.contentStreamId = contentStreamId
        binding.pager.adapter = DetailsPagerAdapter(this, contentItem)
        TabLayoutMediator(binding.tabLayout, binding.pager) { tab, position ->
            run {
                if (position == 0) {
                    if (contentItem.contentTypesId == 3) {
                        tab.text = "Episodes"
                    } else {
                        tab.text = "Similar"
                    }
                } else {
                    tab.text = "Similar"
                }
            }
        }.attach()
    }*/

    private fun castVideoToRemoteMediaClient() {
        val movieMetadata = MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE)

        movieMetadata.putString(MediaMetadata.KEY_TITLE, contentTitle)
        movieMetadata.putString(MediaMetadata.KEY_SUBTITLE, contentDesc)
        movieMetadata.addImage(WebImage(Uri.parse(contentPoster)))

        val mediaInfo = MediaInfo.Builder(videoUrlForCast)
            .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
            .setContentType("videos/mp4")
            .setMetadata(movieMetadata)
            .setStreamDuration(videoDurationLong * 1000)
            .build()
        val remoteMediaClient = mCastSession?.getRemoteMediaClient()
        remoteMediaClient?.load(MediaLoadRequestData.Builder().setMediaInfo(mediaInfo).build())
    }

    private fun getContentDetails() {
        binding.progressIndicator.visibility = View.VISIBLE
        val urlBuilder : HttpUrl.Builder? = HttpUrl.parse(BASE_URL.plus("getContentDetails"))?.newBuilder()
        urlBuilder?.addQueryParameter("authToken", AUTH_TOKEN)
        urlBuilder?.addQueryParameter("permalink", contentPermalink)
        urlBuilder?.addQueryParameter("user_id", userId)

        val url = urlBuilder?.build().toString()
        val request = Request.Builder().url(url).build()

        getOkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "IOException$e")
            }

            override fun onResponse(call: Call, response: okhttp3.Response) {
                runOnUiThread {
                    binding.progressIndicator.visibility = View.GONE
                    handleDetailsResponse(response.body()?.string() ?: "")
                }
            }
        })
    }

    private fun handleDetailsResponse(responseString: String) {
        val jsonObject = JSONObject(responseString)
        val code = jsonObject.optInt("code")
        if (code == 200) {
            val movie = jsonObject.optJSONObject("movie")

            if (movie != null) {
                contentId = movie.optString("id", "")
                contentTypesId = movie.optString("content_types_id", "")
                contentStreamId = movie.optString("movie_stream_id", "")
                movieUniqueId = movie.optString("muvi_uniq_id", "")
                streamUniqueId = movie.optString("movie_stream_uniq_id", "")

                contentPoster = movie.optString("posterForTv", "")
                contentTitle = movie.optString("name", "")
                contentDesc = movie.optString("story", "")
                censorRating = movie.optString("censor_rating", "")
                isConverted = movie.optString("is_converted", "0").toInt()
                if (isConverted == 1 && (contentTypesId == "1" || contentTypesId == "2")) {

                    videoUrlForCast = movie.optString("movieUrlForTv", "")
                    val resolutionArray = movie.getJSONArray("resolution")
                    for (i in 0 until resolutionArray.length()) {
                        if (i == resolutionArray.length() - 1) {
                            val resolutionObj = resolutionArray.getJSONObject(i)
                            videoUrlForDownload = resolutionObj.optString("url", "")
                        }
                    }
                    getDownloadById()
                } else {
                    binding.btnDownload.setButtonActive(false)
                }

                val genre = movie.optString("genre", "")
                val ratingStr = jsonObject.optInt("rating", 0)
                genreText = processGenre(genre)

                //Cast
                val cast = movie.get("cast_detail")
                if (cast !is Boolean) {
                    val castArray = movie.getJSONArray("cast_detail")
                    if (castArray.length() > 0) {
                        castList.clear()
                        for (i in 0 until castArray.length()) {
                            val castObj = castArray.getJSONObject(i)
                            val celebName = castObj.optString("celeb_name", "")
                            val celebId = castObj.optString("celeb_id", "")
                            val castType = castObj.optString("cast_type", "")
                            val celebImage = castObj.optString("celeb_image", "")
                            val permalink = castObj.optString("permalink", "")
                            castList.add(CastDetailItem(celebName, celebId, castType, celebImage, permalink))
                        }
                    }
                }

                //Metadata
                metaDataList.clear()
                val rating = ratingStr.toFloat()
                if (rating > 0) {
                    metaDataList.add(rating.toString())
                }
                val releaseDate = movie.optString("release_date", "")
                val releaseYear = formatReleaseData(releaseDate)
                if (releaseYear.isNotEmpty()) {
                    metaDataList.add(releaseYear)
                }
                val videoDurationStr = movie.optString("video_duration", "")
                videoDurationLong = getVideoDurationInt(videoDurationStr).toLong()
                val videoDuration = formatVideoDuration(videoDurationStr)
                if (videoDuration.isNotEmpty()) {
                    metaDataList.add(videoDuration)
                }
                //Season for Multipart
                if (contentTypesId.toInt() == 3) {
                    val seasonArray = jsonObject.getJSONArray("seasons")
                    for (i in 0 until seasonArray.length()) {
                        val seasonNo = seasonArray.get(i) as String
                        val title = "Season $seasonNo"
                        seasonList.add(SeasonItem("Season $seasonNo", seasonNo))
                    }
                }
                //CTA
                val trailerStatus = movie.optInt("trailer_status", 0)
                binding.btnTrailer.setButtonActive(trailerStatus == 1)

                isFavorite = movie.optInt("is_favorite", 0)
                if (isFavorite == 1) {
                    binding.btnWatchlist.setIconDrawable(ContextCompat.getDrawable(this, R.drawable.ic_done))
                } else {
                    binding.btnWatchlist.setIconDrawable(ContextCompat.getDrawable(this, R.drawable.ic_add))
                }

                val isConverted = movie.optString("is_converted", "0")
                if (isConverted.equals("1")) {
                    binding.btnWatchNow.visibility = View.VISIBLE
                }

                updateDetails()
                if (contentTypesId.toInt() == 1 || contentTypesId.toInt() == 2) {
                    attachSimilarFragment()
                } else if (contentTypesId.toInt() == 3) {
                    attachEpisodeFragment()
                }
            }
        }
    }

    private fun attachSimilarFragment() {
        binding.textViewSimilar.text = "Similar Movies"

        val fm : FragmentManager = supportFragmentManager
        val ft : FragmentTransaction = fm.beginTransaction();
        val fragment = SimilarFragment()
        fragment.arguments = Bundle().apply {
            putString("content_id", contentId)
            putString("content_stream_id", contentStreamId)
        }
        ft.replace(R.id.content_frame, fragment)
        ft.commit()
    }

    private fun attachEpisodeFragment() {
        binding.textViewSimilar.text = "Episodes"
        binding.textViewSeason.text = seasonList[selectedSeasonPos].title
        binding.textViewSimilar.visibility = View.VISIBLE
        binding.textViewSeason.visibility = View.VISIBLE

        val fm : FragmentManager = supportFragmentManager
        val ft : FragmentTransaction = fm.beginTransaction();
        val fragment = EpisodesFragment()
        fragment.arguments = Bundle().apply {
            putString("permalink", contentPermalink)
            putString("series_no", seasonList[selectedSeasonPos].seriesNo)
        }
        ft.replace(R.id.content_frame, fragment)
        ft.commit()
    }

    fun setCurrentSeason(pos: Int) {
        selectedSeasonPos = pos
        attachEpisodeFragment()
    }

    fun showSimilarView() {
        binding.textViewSimilar.visibility = View.VISIBLE
    }

    private fun updateDetails() {
        Glide.with(this).load(contentPoster).into(binding.imageViewPoster)
        binding.textViewContentTitle.text = contentTitle
        binding.textViewGenre.text = genreText
        binding.textViewGenre.isSelected = true
        binding.textViewDesc.text = contentDesc

        if (censorRating.isNotEmpty()) {
            binding.metadataLinear.addView(getRatingView(censorRating))
            binding.metadataLinear.addView(getSeparator())
        }

        for (i in metaDataList.indices) {
            val metaStr: String = metaDataList[i]
            if (i != 0) {
                binding.metadataLinear.addView(getSeparator())
            }
            binding.metadataLinear.addView(getMetaText(metaStr))
        }

        castAdapter.notifyItemInserted(0)

        binding.actionsLinear.visibility = View.VISIBLE

        binding.btnWatchlist.setButtonActive(userId.isNotEmpty())
        /*val rotatingRefresh: Animation = AnimationUtils.loadAnimation(this, R.anim.rotating_refresh)
        rotatingRefresh.repeatCount = Animation.INFINITE
        binding.btnWatchlist.startAnimation(rotatingRefresh)*/
    }

    private fun addToFavorite() {
        binding.progressIndicator.visibility = View.VISIBLE
        val urlBuilder : HttpUrl.Builder? = HttpUrl.parse(BASE_URL.plus("AddtoFavlist"))?.newBuilder()
        urlBuilder?.addQueryParameter("authToken", AUTH_TOKEN)
        urlBuilder?.addQueryParameter("movie_uniq_id", movieUniqueId)
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
                    if (response.isSuccessful && response.body() != null) {
                        val responseStr = response.body()!!.string()
                        val responseObj = JSONObject(responseStr)
                        val code = responseObj.optInt("code", 0)
                        val message = responseObj.optString("msg", "Error")
                        if (code == 200) {
                            isFavorite = 1
                            Toast.makeText(this@ContentDetailsActivity, "Added to watchlist", Toast.LENGTH_SHORT).show()
                            binding.btnWatchlist.setIconDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_done, null))
                        } else {
                            Toast.makeText(this@ContentDetailsActivity, message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        })
    }

    private fun removeFromFavorite() {
        binding.progressIndicator.visibility = View.VISIBLE
        val urlBuilder : HttpUrl.Builder? = HttpUrl.parse(BASE_URL.plus("DeleteFavList"))?.newBuilder()
        urlBuilder?.addQueryParameter("authToken", AUTH_TOKEN)
        urlBuilder?.addQueryParameter("movie_uniq_id", movieUniqueId)
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
                    if (response.isSuccessful && response.body() != null) {
                        val responseStr = response.body()!!.string()
                        val responseObj = JSONObject(responseStr)
                        val code = responseObj.optInt("code", 0)
                        val message = responseObj.optString("msg", "Error")
                        if (code == 200) {
                            isFavorite = 0
                            Toast.makeText(this@ContentDetailsActivity, "Removed from watchlist", Toast.LENGTH_SHORT).show()
                            binding.btnWatchlist.setIconDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_add, null))
                        } else {
                            Toast.makeText(this@ContentDetailsActivity, message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        })
    }

    private fun getDownloadById() {
        GlobalScope.launch {
            val downloadItems = downloadDao.getDownloadById(streamUniqueId)
            if (downloadItems.isNotEmpty()) {
                isDownloaded = true
                binding.btnDownload.setText("Downloaded")
                binding.btnDownload.setIconDrawable(ContextCompat.getDrawable(this@ContentDetailsActivity, R.drawable.download_done))
            }
        }
    }

    private fun getMetaText(meta_data: String): TextView {
        val textView = TextView(this)
        textView.text = meta_data
        //textView.setTextColor(ContextCompat.getColor(this, R.color.white))
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
        val typeface = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            resources.getFont(R.font.sf_ui_text_regular)
        } else {
            ResourcesCompat.getFont(this, R.font.sf_ui_text_regular)
        }
        textView.typeface = typeface
        return textView
    }

    private fun getSeparator(): View {
        val view = View(this)
        //view.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
        val layoutParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
            Util.convertDpToPixel(this, 8),
            Util.convertDpToPixel(this, 10)
        )
        layoutParams.marginStart = Util.convertDpToPixel(this, 2)
        layoutParams.marginEnd = Util.convertDpToPixel(this, 2)
        view.layoutParams = layoutParams
        return view
    }

    private fun getRatingView(meta_data: String): View {
        val textView = TextView(this)
        textView.text = meta_data
        //textView.setTextColor(ContextCompat.getColor(this, R.color.white))
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
        val typeface = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            resources.getFont(R.font.sf_ui_text_bold)
        } else {
            ResourcesCompat.getFont(this, R.font.sf_ui_text_bold)
        }
        textView.typeface = typeface
        val layoutParams = FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        layoutParams.leftMargin = Util.convertDpToPixel(this, 8)
        layoutParams.rightMargin = Util.convertDpToPixel(this, 8)
        textView.layoutParams = layoutParams

        val parentFrame = FrameLayout(this)
        parentFrame.background = ContextCompat.getDrawable(this, R.drawable.rating_bg)
        val parentLayoutParams = FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        parentLayoutParams.marginEnd = Util.convertDpToPixel(this, 2)
        parentFrame.layoutParams = parentLayoutParams

        parentFrame.addView(textView)
        return parentFrame
    }

    private fun processGenre(data: String?) : String {
        var returnString = ""
        var genreString : String = data.toString()
        genreString = genreString.replace("\"", "")
        genreString = genreString.replace("]", "")
        genreString = genreString.replace("[", "")
        val genreItems = genreString.split(",")
        for (i in genreItems.indices) {
            val item = genreItems[i]
            val smallCase = item.lowercase(Locale.getDefault())
            val sentenceCase = smallCase.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
            if (i != 0) {
                returnString = "$returnString, "
            }
            returnString += sentenceCase
        }
        return returnString
    }

    private fun formatReleaseData(releaseDate: String): String {
        if (releaseDate.isEmpty()) {
            return ""
        }
        val splitArray = releaseDate.split("-")
        val year = splitArray[0]
        return year
    }

    private fun getVideoDurationInt(durationStr: String): Int {
        return try {
            val hr = durationStr.substring(0, 2).toInt()
            val min = durationStr.substring(3, 5).toInt()
            val sec = durationStr.substring(6, 8).toInt()
            val totalDurMin = hr * 60 + min
            val totalDurSec = hr * 60 * 60 + min * 60 + sec
            totalDurSec
        } catch (e: Exception) {
            0
        }
    }

    private fun updateUI() {
        val layoutParams = binding.btnWatchNow.layoutParams as CoordinatorLayout.LayoutParams
        layoutParams.width = Util.convertDpToPixel(this@ContentDetailsActivity, 120)
        layoutParams.bottomMargin = Util.convertDpToPixel(this@ContentDetailsActivity, 80)
        layoutParams.gravity = (Gravity.BOTTOM or Gravity.END)
        binding.btnWatchNow.layoutParams = layoutParams
        binding.btnWatchNow.text = "Cast"
        binding.btnWatchNow.icon = ContextCompat.getDrawable(this, R.drawable.ic_cast)
    }

    private fun revertUI() {
        val layoutParams = binding.btnWatchNow.layoutParams as CoordinatorLayout.LayoutParams
        layoutParams.width = MATCH_PARENT
        layoutParams.bottomMargin = Util.convertDpToPixel(this@ContentDetailsActivity, 20)
        layoutParams.gravity = (Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL)
        binding.btnWatchNow.layoutParams = layoutParams
        binding.btnWatchNow.text = "Watch Now"
        binding.btnWatchNow.icon = ContextCompat.getDrawable(this, R.drawable.ic_play_arrow)
    }

    override fun onResume() {
        super.onResume()
        mCastSession = mSessionManager.currentCastSession
        mSessionManager.addSessionManagerListener(mSessionManagerListener, CastSession::class.java)
    }

    override fun onPause() {
        super.onPause()
        mSessionManager.removeSessionManagerListener(mSessionManagerListener, CastSession::class.java)
        mCastSession = null
    }

    private inner class SessionManagerListenerImpl : SessionManagerListener<CastSession> {
        override fun onSessionStarted(session: CastSession, sessionId: String) {
            Log.d(TAG, "Cast Session Started")
            castVideoToRemoteMediaClient()
            updateUI()
        }

        override fun onSessionResumed(session: CastSession, wasSuspended: Boolean) {
            Log.d(TAG, "Cast Session Resumed")
            updateUI()
        }

        override fun onSessionEnded(session: CastSession, error: Int) {
            Log.d(TAG, "Cast Session Ended")
            revertUI()
        }

        override fun onSessionEnding(p0: CastSession) {
        }

        override fun onSessionResumeFailed(p0: CastSession, p1: Int) {
        }

        override fun onSessionResuming(p0: CastSession, p1: String) {
        }

        override fun onSessionStartFailed(p0: CastSession, p1: Int) {
        }

        override fun onSessionStarting(p0: CastSession) {
        }

        override fun onSessionSuspended(p0: CastSession, p1: Int) {
        }
    }

    private inner class ItemDecoration : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            super.getItemOffsets(outRect, view, parent, state)
            if (parent.getChildAdapterPosition(view) == 0) {
                outRect.set(
                    Util.convertDpToPixel(parent.context, 20), 0,
                    Util.convertDpToPixel(parent.context, 6), 0)
            } else if (parent.getChildAdapterPosition(view) == parent.adapter!!.itemCount - 1) {
                outRect.set(
                    Util.convertDpToPixel(parent.context, 6), 0,
                    Util.convertDpToPixel(parent.context, 20), 0)
            } else {
                outRect.set(
                    Util.convertDpToPixel(parent.context, 6), 0,
                    Util.convertDpToPixel(parent.context, 6), 0)
            }
        }
    }

    private inner class SimilarItemDecoration : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            super.getItemOffsets(outRect, view, parent, state)
            if (parent.getChildAdapterPosition(view) == 0) {
                outRect.set(
                    Util.convertDpToPixel(parent.context, 20), 0,
                    Util.convertDpToPixel(parent.context, 6), 0)
            } else if (parent.getChildAdapterPosition(view) == parent.adapter!!.itemCount - 1) {
                outRect.set(
                    Util.convertDpToPixel(parent.context, 6), 0,
                    Util.convertDpToPixel(parent.context, 20), 0)
            } else {
                outRect.set(
                    Util.convertDpToPixel(parent.context, 6), 0,
                    Util.convertDpToPixel(parent.context, 6), 0)
            }
        }
    }
}