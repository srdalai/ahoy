package com.sdtechnocrat.ahoy.views.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sdtechnocrat.ahoy.adapters.CategoryAdapter
import com.sdtechnocrat.ahoy.adapters.ContentGridAdapter
import com.sdtechnocrat.ahoy.api.OkHttpBuilder.getOkHttpClient
import com.sdtechnocrat.ahoy.data.CategoryItem
import com.sdtechnocrat.ahoy.data.ContentItem
import com.sdtechnocrat.ahoy.databinding.FragmentCategoryBinding
import com.sdtechnocrat.ahoy.utilities.Util.Companion.AUTH_TOKEN
import com.sdtechnocrat.ahoy.utilities.Util.Companion.BASE_URL
import com.sdtechnocrat.ahoy.views.ContentDetailsActivity
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class CategoryFragment : Fragment() {

    private var _binding : FragmentCategoryBinding? = null
    private val binding get() = _binding!!

    private val categoryList = mutableListOf<CategoryItem>()
    lateinit var categoryAdapter: CategoryAdapter

    val contentList = mutableListOf<ContentItem>()
    lateinit var contentAdapter: ContentGridAdapter

    lateinit var permalink: String

    private val limit = 8;
    private var offset = 1;
    var totalContentCount = 0
    var isLoading = false
    var isLastPage = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentCategoryBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
        setupRecyclers()
        getCategories()
    }

    private fun setupListeners() {
        binding.imageViewFilter.setOnClickListener {

        }
    }

    private fun setupRecyclers() {
        categoryAdapter = CategoryAdapter(categoryList, object :
            CategoryAdapter.OnItemClickListener {
            override fun onItemClicked(position: Int, categoryItem: CategoryItem) {
                categoryAdapter.setSelected(position)
                permalink = categoryItem.permalink
                val size = contentList.size
                contentList.clear()
                contentAdapter.notifyItemRangeRemoved(0, size)
                getContentList()
            }
        })
        binding.categoryRecycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.categoryRecycler.adapter = categoryAdapter

        contentAdapter = ContentGridAdapter(requireContext(), contentList, object :
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
        val layoutManager = GridLayoutManager(requireContext(), 2)
        binding.contentRecycler.layoutManager = layoutManager
        binding.contentRecycler.adapter = contentAdapter
        binding.contentRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val visibleItemCount: Int = layoutManager.childCount
                val totalItemCount: Int = layoutManager.itemCount
                val firstVisibleItemPosition: Int = layoutManager.findFirstVisibleItemPosition()
                /*Log.d("TAG",
                    "visibleItemCount -> $visibleItemCount, totalItemCount -> $totalItemCount, firstVisibleItemPosition -> $firstVisibleItemPosition"
                )*/

                if (!isLoading && !isLastPage) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                        && firstVisibleItemPosition >= 0
                        && totalItemCount < totalContentCount) {
                        //loadMoreItems();
                    }
                }
                isLastPage = totalItemCount == totalContentCount
            }
        })
    }

    private fun loadMoreItems() {
        Toast.makeText(requireContext(), "Getting more items", Toast.LENGTH_SHORT).show()
        offset++
        getContentList()
    }

    private fun getCategories() {
        binding.progressIndicator.visibility = View.VISIBLE
        val urlBuilder = HttpUrl.parse(BASE_URL.plus("getCategoryList"))?.newBuilder()
        urlBuilder?.addQueryParameter("authToken", AUTH_TOKEN)
        urlBuilder?.addQueryParameter("content_flag", "0")
        val url = urlBuilder?.build().toString()
        val request = Request.Builder().url(url).build()
        getOkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                TODO("Not yet implemented")
            }

            override fun onResponse(call: Call, response: Response) {
                requireActivity().runOnUiThread {
                    binding.progressIndicator.visibility = View.GONE
                    if (response.isSuccessful) {
                        handleCategoryResponse(response.body()?.string()?: "")
                    }
                }
            }
        })
    }

    private fun handleCategoryResponse(responseStr: String) {
        val responseObj = JSONObject(responseStr)
        val code = responseObj.optInt("code", 0)
        if (code == 200) {
            val catArray = responseObj.getJSONArray("category_list")
            for (i in 0 until catArray.length()) {
                val catObj = catArray.getJSONObject(i)
                val name = catObj.optString("category_name", "")
                val permalink = catObj.optString("permalink", "")
                val catId = catObj.optString("category_id", "")
                categoryList.add(CategoryItem(name, permalink, catId))
            }
            categoryAdapter.notifyItemInserted(0)

            if (categoryList.size > 0) {
                permalink = categoryList[0].permalink
                getContentList()
            }
        }
    }

    private fun getContentList() {
        isLoading = true
        binding.progressIndicator.visibility = View.VISIBLE
        val urlBuilder = HttpUrl.parse(BASE_URL.plus("getContentList"))?.newBuilder()
        urlBuilder?.addQueryParameter("authToken", AUTH_TOKEN)
        urlBuilder?.addQueryParameter("permalink", permalink)
        urlBuilder?.addQueryParameter("limit", limit.toString())
        urlBuilder?.addQueryParameter("offset", offset.toString())
        val url = urlBuilder?.build().toString()
        val request = Request.Builder().url(url).build()

        getOkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                isLoading = false
                TODO("Not yet implemented")
            }

            override fun onResponse(call: Call, response: Response) {
                requireActivity().runOnUiThread {
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
        val status = responseObj.optInt("status", 0)
        val message = responseObj.optString("msg", "Error")
        val _contentList = mutableListOf<ContentItem>()
        if (status == 200) {
            totalContentCount = responseObj.optString("item_count", "0").toInt()
            val movieArray = responseObj.getJSONArray("movieList")
            if (movieArray.length() > 0) {
                for (i in 0 until movieArray.length()) {
                    val movieObj = movieArray.getJSONObject(i)
                    val permalink = movieObj.optString("permalink", "")
                    val title = movieObj.optString("list_name", "")
                    val poster = movieObj.optString("posterForTv", "")
                    _contentList.add(ContentItem(permalink, title, poster))
                }
            }
        }

        if (_contentList.size > 0) {
            val size = contentList.size
            contentList.addAll(_contentList)
            contentAdapter.notifyItemInserted(size)
            binding.contentRecycler.visibility = View.VISIBLE
        }
        isLoading = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}