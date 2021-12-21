package com.sdtechnocrat.ahoy.views.fragments

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sdtechnocrat.ahoy.R
import com.sdtechnocrat.ahoy.adapters.ContentListAdapter
import com.sdtechnocrat.ahoy.api.OkHttpBuilder.getOkHttpClient
import com.sdtechnocrat.ahoy.data.ContentItem
import com.sdtechnocrat.ahoy.data.HistoryItem
import com.sdtechnocrat.ahoy.databinding.FragmentSearchBinding
import com.sdtechnocrat.ahoy.room.HistoryDatabase
import com.sdtechnocrat.ahoy.room.HistoryDbHelperImpl
import com.sdtechnocrat.ahoy.utilities.Util
import com.sdtechnocrat.ahoy.utilities.Util.Companion.AUTH_TOKEN
import com.sdtechnocrat.ahoy.utilities.Util.Companion.BASE_URL
import com.sdtechnocrat.ahoy.viewmodels.ViewModelFactory
import com.sdtechnocrat.ahoy.viewmodels.SearchFragmentViewModel
import com.sdtechnocrat.ahoy.views.ContentDetailsActivity
import com.sdtechnocrat.ahoy.views.MainActivity
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.util.*

class SearchFragment : Fragment() {

    private val TAG = "SearchFragment"

    private var _binding : FragmentSearchBinding? = null
    val binding get() = _binding!!

    lateinit var queryString : String

    private val contentList = mutableListOf<ContentItem>()
    lateinit var contentAdapter : ContentListAdapter

    private val historyList = mutableListOf<HistoryItem>()
    lateinit var historyAdapter : SearchHistoryAdapter

    lateinit var viewModel: SearchFragmentViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSearchBinding.inflate(layoutInflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupObserver()

        binding.editTextSearch.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(textView: TextView?, p1: Int, event: KeyEvent?): Boolean {
                queryString = textView?.text.toString().lowercase(Locale.getDefault())
                binding.editTextSearch.hideKeyboard()
                initiateSearch()
                return true
            }
        })

        binding.imageViewMic.setOnClickListener {
            (requireActivity() as MainActivity).displaySpeechRecognizer()
        }

        setupRecyclers()
    }

    private fun setupObserver() {
        viewModel.getHistory().observe(viewLifecycleOwner, {
            updateRecyclerView(it)
        })
    }

    private fun setupRecyclers() {
        contentAdapter = ContentListAdapter(requireContext(), contentList, object :
            ContentListAdapter.OnItemClickListener {
            override fun onItemClicked(contentItem: ContentItem) {
                val intent = Intent(requireContext(), ContentDetailsActivity::class.java).apply {
                    putExtra("content_permalink", contentItem.permalink)
                    putExtra("content_title", contentItem.title)
                    putExtra("content_poster", contentItem.poster)
                }
                requireContext().startActivity(intent)
            }
        })
        binding.itemsRecycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.itemsRecycler.adapter = contentAdapter
        binding.itemsRecycler.addItemDecoration(ItemDecoration())

        //Setting up Search History Recycler
        historyAdapter = SearchHistoryAdapter(historyList, object :
            SearchHistoryAdapter.OnItemClickListener {
            override fun onItemClicked(historyItem: HistoryItem) {
                //viewModel.updateHistory(historyItem)
                queryString = historyItem.searchTerm
                binding.editTextSearch.setText(queryString)
                initiateSearch()
            }
        })
        binding.historyListView.layoutManager = LinearLayoutManager(requireContext())
        binding.historyListView.adapter = historyAdapter
    }

    private fun updateRecyclerView(list: List<HistoryItem>?) {
        if (list == null) return
        historyList.clear()
        historyAdapter.notifyDataSetChanged()
        historyList.addAll(list)
        historyAdapter.notifyItemInserted(0)
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(this,
            ViewModelFactory(
                HistoryDbHelperImpl(HistoryDatabase.getInstance(requireContext().applicationContext))
            )
        ).get(SearchFragmentViewModel::class.java)
    }

    fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    fun initiateSearch() {
        if (::contentAdapter.isInitialized) {
            val size = contentList.size
            contentList.clear()
            contentAdapter.notifyItemRangeRemoved(0, size)
        }
        binding.textViewNoContent.visibility = View.GONE
        viewModel.insertHistory(queryString)
        getSearchData()
    }

    fun searchByVoice(searchTerm: String) {
        queryString = searchTerm.toString().lowercase(Locale.getDefault())
        binding.editTextSearch.setText(queryString)
        initiateSearch()
    }

    private fun getSearchData() {
        binding.progressIndicator.visibility = View.VISIBLE
        val urlBuilder : HttpUrl.Builder? = HttpUrl.parse(BASE_URL.plus("searchData"))?.newBuilder()
        urlBuilder?.addQueryParameter("authToken", AUTH_TOKEN)
        urlBuilder?.addQueryParameter("q", queryString)
        urlBuilder?.addQueryParameter("season_info", "0")
        urlBuilder?.addQueryParameter("is_child_required", "0")

        val url = urlBuilder?.build()!!

        val request = Request.Builder().url(url).build()
        getOkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                TODO("Not yet implemented")
            }

            override fun onResponse(call: Call, response: Response) {
                requireActivity().runOnUiThread {
                    binding.progressIndicator.visibility = View.GONE
                    if (response.isSuccessful) {
                        handleSearchResponse(response.body()?.string().toString())
                    }
                }
            }
        })
    }

    private fun handleSearchResponse(responseStr: String) {
        val responseObj = JSONObject(responseStr)
        val code = responseObj.optInt("code", 0)
        val message = responseObj.optString("msg", "Error")
        if (code == 200) {
            val searchDataArray = responseObj.getJSONArray("search")
            if (searchDataArray.length() > 0) {
                for (i in 0 until searchDataArray.length()) {
                    val contentObj = searchDataArray.getJSONObject(i)
                    val permalink = contentObj.optString("permalink", "")
                    val title = contentObj.optString("title", "")
                    val poster = contentObj.optString("posterForTv", "")
                    contentList.add(ContentItem(permalink, title, poster))
                }
                contentAdapter.notifyItemInserted(0)
                binding.textViewSearch.visibility = View.VISIBLE
            } else {
                binding.textViewNoContent.visibility = View.VISIBLE
                binding.textViewSearch.visibility = View.GONE
            }
        } else {
            binding.textViewNoContent.visibility = View.VISIBLE
            binding.textViewSearch.visibility = View.GONE
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
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

    class SearchHistoryAdapter(private val list: List<HistoryItem>, val onItemClickListener: OnItemClickListener) : RecyclerView.Adapter<SearchHistoryAdapter.SearchHistoryViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchHistoryViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_search_history_list_layout, parent, false)
            return SearchHistoryViewHolder(view)
        }

        override fun onBindViewHolder(holder: SearchHistoryViewHolder, position: Int) {
            val item = list[position]
            holder.textView.text = item.searchTerm
            holder.bind(item, onItemClickListener)
        }

        override fun getItemCount(): Int {
            return list.size
        }

        class SearchHistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val textView : TextView = itemView.findViewById(R.id.text_view)

            fun bind(historyItem: HistoryItem, listener: OnItemClickListener) {
                itemView.setOnClickListener {
                    listener.onItemClicked(historyItem)
                }
            }
        }
        
        interface OnItemClickListener {
            fun onItemClicked(historyItem: HistoryItem)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}