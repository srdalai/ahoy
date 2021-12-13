package com.sdtechnocrat.ahoy.views.fragments

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sdtechnocrat.ahoy.R
import com.sdtechnocrat.ahoy.adapters.ContentListAdapter
import com.sdtechnocrat.ahoy.adapters.ProfileActionAdapter
import com.sdtechnocrat.ahoy.api.OkHttpBuilder.getOkHttpClient
import com.sdtechnocrat.ahoy.data.ContentItem
import com.sdtechnocrat.ahoy.data.ProfileActionItem
import com.sdtechnocrat.ahoy.databinding.FragmentProfileBinding
import com.sdtechnocrat.ahoy.utilities.SharedPref
import com.sdtechnocrat.ahoy.utilities.Util.Companion.AUTH_TOKEN
import com.sdtechnocrat.ahoy.utilities.Util.Companion.BASE_URL
import com.sdtechnocrat.ahoy.utilities.Util.Companion.convertDpToPixel
import com.sdtechnocrat.ahoy.views.AccountActivity
import com.sdtechnocrat.ahoy.views.ContentDetailsActivity
import com.sdtechnocrat.ahoy.views.ContentListingActivity
import com.sdtechnocrat.ahoy.views.ContentListingActivity.Companion.ACTION_WATCH_HISTORY
import com.sdtechnocrat.ahoy.views.ContentListingActivity.Companion.ACTION_WATCH_LIST
import com.sdtechnocrat.ahoy.views.LoginActivity
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPref: SharedPref

    private lateinit var userId: String
    private val profileActionItemList = mutableListOf<ProfileActionItem>()
    val contentList = mutableListOf<ContentItem>()
    lateinit var watchListAdapter: ContentListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPref = SharedPref.getInstance(requireContext())

        userId = sharedPref.getUserId()!!

        //setup Profile Items
        setProfileActionItems()

        binding.btnLogin.setOnClickListener {
            val intent = Intent(requireContext(), LoginActivity::class.java).apply {
                putExtra("action", "login")
            }
            requireActivity().startActivity(intent)
        }

        binding.watchlistRecycler.addItemDecoration(ItemDecoration())

        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        watchListAdapter = ContentListAdapter(requireContext(), contentList, object : ContentListAdapter.OnItemClickListener {
            override fun onItemClicked(contentItem: ContentItem) {
                val intent = Intent(requireContext(), ContentDetailsActivity::class.java).apply {
                    putExtra("content_permalink", contentItem.permalink)
                    putExtra("content_title", contentItem.title)
                    putExtra("content_poster", contentItem.poster)
                }
                requireContext().startActivity(intent)
            }
        })
        binding.watchlistRecycler.layoutManager = layoutManager
        binding.watchlistRecycler.adapter = watchListAdapter

        if (userId.isEmpty()) {
            binding.btnLogin.visibility = View.VISIBLE
        } else {
            binding.textViewProfile.text = sharedPref.getUserName()
            getFavorites()
        }

    }

    private fun setProfileActionItems() {
        if (userId.isNotEmpty()) {
            profileActionItemList.add(
                ProfileActionItem(
                    "Watch List",
                    "watch-list",
                    R.drawable.ic_playlist_add
                )
            )
            profileActionItemList.add(
                ProfileActionItem(
                    "Watch History",
                    "history",
                    R.drawable.ic_history
                )
            )
            profileActionItemList.add(
                ProfileActionItem(
                    "Downloads",
                    "downloads",
                    R.drawable.ic_cloud_download
                )
            )
            profileActionItemList.add(
                ProfileActionItem(
                    "Account",
                    "account",
                    R.drawable.ic_account_circle
                )
            )
            profileActionItemList.add(
                ProfileActionItem(
                    "Subscription",
                    "subscription",
                    R.drawable.ic_payment
                )
            )
        }
        profileActionItemList.add(
            ProfileActionItem(
                "App Settings",
                "app-settings",
                R.drawable.ic_settings
            )
        )
        profileActionItemList.add(ProfileActionItem("Help", "help", R.drawable.ic_contact_support))
        profileActionItemList.add(
            ProfileActionItem(
                "Privacy & Terms",
                "privacy-terms",
                R.drawable.ic_privacy_terms
            )
        )

        val layoutManager = LinearLayoutManager(requireContext())
        val adapter = ProfileActionAdapter(profileActionItemList, object :
            ProfileActionAdapter.OnItemClickListener {
            override fun onItemClicked(profileActionItem: ProfileActionItem) {
                handleProfileItemAction(profileActionItem)
            }

        })
        binding.profileActionsListView.layoutManager = layoutManager
        binding.profileActionsListView.adapter = adapter
    }

    private fun handleProfileItemAction(actionItem: ProfileActionItem) {
        when(actionItem.slug) {
            "watch-list" -> {
                val intent = Intent(requireContext(), ContentListingActivity::class.java).apply {
                    putExtra("action", ACTION_WATCH_LIST)
                }
                startActivity(intent)
            }
            "history" -> {
                val intent = Intent(requireContext(), ContentListingActivity::class.java).apply {
                    putExtra("action", ACTION_WATCH_HISTORY)
                }
                startActivity(intent)
            }
            "account" -> {
                val intent = Intent(requireContext(), AccountActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun getFavorites() {
        binding.progressIndicator.visibility = View.VISIBLE
        val urlBuilder: HttpUrl.Builder? =
            HttpUrl.parse(BASE_URL.plus("ViewFavourite"))?.newBuilder()
        urlBuilder?.addQueryParameter("authToken", AUTH_TOKEN)
        urlBuilder?.addQueryParameter("user_id", userId)
        val url = urlBuilder?.build().toString()
        val request = Request.Builder()
            .url(url)
            .build()
        getOkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                TODO("Not yet implemented")
            }

            override fun onResponse(call: Call, response: Response) {
                requireActivity().runOnUiThread {
                    binding.progressIndicator.visibility = View.GONE
                    if (response.isSuccessful) {
                        handleFavoriteData(response.body()?.string() ?: "")
                    }
                }
            }

        })

    }

    private fun handleFavoriteData(responseStr: String) {
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

                watchListAdapter.notifyItemInserted(0)
                binding.watchlistRecycler.visibility = View.VISIBLE
                binding.textViewWatchList.visibility = View.VISIBLE
            }
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
                outRect.set(convertDpToPixel(parent.context, 20), 0, convertDpToPixel(parent.context, 6), 0)
            } else if (parent.getChildAdapterPosition(view) == parent.adapter!!.itemCount - 1) {
                outRect.set(convertDpToPixel(parent.context, 6), 0, convertDpToPixel(parent.context, 20), 0)
            } else {
                outRect.set(convertDpToPixel(parent.context, 6), 0, convertDpToPixel(parent.context, 6), 0)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}