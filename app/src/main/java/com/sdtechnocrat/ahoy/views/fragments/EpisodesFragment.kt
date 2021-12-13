package com.sdtechnocrat.ahoy.views.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.sdtechnocrat.ahoy.R
import com.sdtechnocrat.ahoy.adapters.EpisodeAdapter
import com.sdtechnocrat.ahoy.api.OkHttpBuilder.getOkHttpClient
import com.sdtechnocrat.ahoy.data.EpisodeItem
import com.sdtechnocrat.ahoy.databinding.FragmentEpisodesBinding
import com.sdtechnocrat.ahoy.utilities.Util.Companion.AUTH_TOKEN
import com.sdtechnocrat.ahoy.utilities.Util.Companion.BASE_URL
import com.sdtechnocrat.ahoy.utilities.Util.Companion.formatVideoDuration
import com.sdtechnocrat.ahoy.views.ContentDetailsActivity
import com.sdtechnocrat.ahoy.views.PrePlayingActivity
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class EpisodesFragment : Fragment() {

    private var _binding: FragmentEpisodesBinding? = null
    private val binding get() = _binding!!

    private val episodeList = mutableListOf<EpisodeItem>()
    lateinit var episodeAdapter: EpisodeAdapter

    lateinit var contentPermalink: String
    lateinit var seriesNumber: String
    lateinit var movieUniqueId: String
    lateinit var streamUniqueId: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentEpisodesBinding.inflate(layoutInflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        contentPermalink = arguments?.getString("permalink", "").toString()
        seriesNumber = arguments?.getString("series_no", "1").toString()

        setupEpisodeRecycler()

        getEpisodes()
    }

    private fun setupEpisodeRecycler() {
        episodeAdapter = EpisodeAdapter(requireContext(), episodeList, object :
            EpisodeAdapter.OnItemClickListener {
            override fun onItemClicked(episodeItem: EpisodeItem) {
                streamUniqueId = episodeItem.streamUniqueId
                startPlayback()
            }
        })
        binding.episodeRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.episodeRecycler.adapter = episodeAdapter
    }

    private fun getEpisodes() {
        val urlBuilder = HttpUrl.parse(BASE_URL.plus("episodeDetails"))?.newBuilder()
        urlBuilder?.addQueryParameter("authToken", AUTH_TOKEN)
        urlBuilder?.addQueryParameter("permalink", contentPermalink)
        urlBuilder?.addQueryParameter("series_number", seriesNumber)
        val url = urlBuilder?.build().toString()
        val request = Request.Builder().url(url).build()
        getOkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                TODO("Not yet implemented")
            }

            override fun onResponse(call: Call, response: Response) {
                requireActivity().runOnUiThread {
                    if (response.isSuccessful) {
                        handleEpisodesResponse(response.body()?.string()?: "")
                    }
                }
            }

        })
    }

    private fun handleEpisodesResponse(responseStr: String) {
        val responseObj = JSONObject(responseStr)
        val code = responseObj.optInt("code", 0)
        if (code == 200) {
            movieUniqueId = responseObj.getString("muvi_uniq_id")
            val episodesArray = responseObj.getJSONArray("episode")
            for (i in 0 until episodesArray.length()) {
                val episodeObj = episodesArray.getJSONObject(i)
                val title = episodeObj.optString("episode_title", "")
                val story = episodeObj.optString("episode_story", "")
                val poster = episodeObj.optString("posterForTv", "")
                val streamId = episodeObj.optString("movie_stream_uniq_id", "")
                val videoDurationStr = episodeObj.optString("video_duration", "")

                val episodeItem = EpisodeItem()
                episodeItem.title = title
                episodeItem.story = story
                episodeItem.poster = poster
                episodeItem.streamUniqueId = streamId
                episodeItem.duration = formatVideoDuration(videoDurationStr)
                episodeList.add(episodeItem)

            }
            episodeAdapter.notifyItemInserted(0)
        }
    }

    private fun startPlayback() {
        val intent = Intent(requireContext(), PrePlayingActivity::class.java).apply {
            putExtra("muvi_unique_id", movieUniqueId)
            putExtra("stream_unique_id", streamUniqueId)
        }
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}