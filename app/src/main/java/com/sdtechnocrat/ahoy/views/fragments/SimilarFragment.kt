package com.sdtechnocrat.ahoy.views.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.sdtechnocrat.ahoy.R
import com.sdtechnocrat.ahoy.adapters.ContentGridAdapter
import com.sdtechnocrat.ahoy.api.OkHttpBuilder
import com.sdtechnocrat.ahoy.data.ContentItem
import com.sdtechnocrat.ahoy.databinding.FragmentSimilarBinding
import com.sdtechnocrat.ahoy.utilities.Util
import com.sdtechnocrat.ahoy.views.ContentDetailsActivity
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class SimilarFragment : Fragment() {

    private var _binding : FragmentSimilarBinding? = null
    private val binding get() = _binding!!

    private val relatedContentList : MutableList<ContentItem> = mutableListOf()
    private lateinit var relatedContentAdapter : ContentGridAdapter

    private lateinit var contentId : String
    private lateinit var contentStreamId : String


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSimilarBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        contentId = arguments?.getString("content_id").toString()
        contentStreamId = arguments?.getString("content_stream_id").toString()

        binding.similarRecycler.layoutManager = GridLayoutManager(requireContext(), 2)
        relatedContentAdapter = ContentGridAdapter(requireContext(), relatedContentList, object :
            ContentGridAdapter.OnItemClickListener {
            override fun onItemClicked(contentItem: ContentItem) {
                val intent = Intent(requireContext(), ContentDetailsActivity::class.java).apply {
                    putExtra("content_permalink", contentItem.permalink)
                    putExtra("content_title", contentItem.title)
                    putExtra("content_poster", contentItem.poster)
                }
                startActivity(intent)
            }

        })
        binding.similarRecycler.adapter = relatedContentAdapter
        //binding.similarRecycler.addItemDecoration(SimilarItemDecoration())

        getSimilarContents()
    }

    private fun getSimilarContents() {
        val urlBuilder : HttpUrl.Builder? = HttpUrl.parse(Util.BASE_URL.plus("RelatedContent"))?.newBuilder()
        urlBuilder?.addQueryParameter("authToken", Util.AUTH_TOKEN)
        urlBuilder?.addQueryParameter("content_id", contentId)
        urlBuilder?.addQueryParameter("content_stream_id", contentStreamId)
        val url = urlBuilder?.build().toString()
        val request = Request.Builder().url(url).build()
        OkHttpBuilder.getOkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                TODO("Not yet implemented")
            }

            override fun onResponse(call: Call, response: Response) {
                requireActivity().runOnUiThread {
                    //binding.progressIndicator.visibility = View.GONE
                    handleRelatedResponse(response.body()?.string()?: "")
                }
            }

        })
    }

    private fun handleRelatedResponse(responseStr: String) {
        val jsonObject = JSONObject(responseStr)
        val code = jsonObject.optInt("code")
        if (code == 200) {
            val contentArray = jsonObject.optJSONArray("contentData")
            if (contentArray != null) {
                for (i in 0 until contentArray.length()) {
                    val contentObj = contentArray.getJSONObject(i)
                    val permalink = contentObj.optString("c_permalink", "")
                    val title = contentObj.optString("title", "")
                    val poster = contentObj.optString("posterForTv", "")
                    relatedContentList.add(ContentItem(permalink, title, poster))
                }
                relatedContentAdapter.notifyItemInserted(0)
                (requireActivity() as ContentDetailsActivity).showSimilarView()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}