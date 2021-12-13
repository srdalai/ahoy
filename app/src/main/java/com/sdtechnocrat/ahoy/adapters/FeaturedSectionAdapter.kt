package com.sdtechnocrat.ahoy.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sdtechnocrat.ahoy.R
import com.sdtechnocrat.ahoy.data.FeaturedContentItem
import com.sdtechnocrat.ahoy.data.FeaturedSectionItem
import com.sdtechnocrat.ahoy.utilities.Util
import com.sdtechnocrat.ahoy.views.ContentDetailsActivity

class FeaturedSectionAdapter(val context: Activity, val sectionList: List<FeaturedSectionItem?>?) : RecyclerView.Adapter<FeaturedSectionAdapter.FeaturedSectionViewHolder>() {

    // An object of RecyclerView.RecycledViewPool
    // is created to share the Views
    // between the child and
    // the parent RecyclerViews
    val viewPool : RecyclerView.RecycledViewPool = RecyclerView.RecycledViewPool()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeaturedSectionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_featured_section_layout, parent, false)
        return FeaturedSectionViewHolder(view)
    }

    override fun onBindViewHolder(holder: FeaturedSectionViewHolder, position: Int) {
        val sectionItem = sectionList?.get(position)
        holder.sectionTitleTextView.text = sectionItem?.title ?: ""

        // Here we have assigned the layout
        // as LinearLayout with vertical orientation
        val layoutManager = LinearLayoutManager(holder.sectionContentRecycler.context, LinearLayoutManager.HORIZONTAL, false)

        // Since this is a nested layout, so
        // to define how many child items
        // should be prefetched when the
        // child RecyclerView is nested
        // inside the parent RecyclerView,
        // we use the following method
        layoutManager.initialPrefetchItemCount = sectionItem?.sectionContents?.size ?: 0

        // Create an instance of the child
        // item view adapter and set its
        // adapter, layout manager and RecyclerViewPool
        val contentAdapter = FeaturedContentAdapter(context, sectionItem?.sectionContents, object :
            FeaturedContentAdapter.OnItemClickListener {
            override fun onItemClicked(contentItem: FeaturedContentItem) {
                val intent = Intent(context, ContentDetailsActivity::class.java).apply {
                    putExtra("content_permalink", contentItem.permalink)
                    putExtra("content_title", contentItem.name)
                    putExtra("content_poster", contentItem.posterForTv)
                }
                context.startActivity(intent)
            }

        })
        holder.sectionContentRecycler.addItemDecoration(ItemDecoration())
        holder.sectionContentRecycler.layoutManager = layoutManager
        holder.sectionContentRecycler.adapter = contentAdapter
        holder.sectionContentRecycler.setRecycledViewPool(viewPool)
    }

    override fun getItemCount(): Int {
        return sectionList?.size ?: 0
    }

    class FeaturedSectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sectionTitleTextView : TextView = itemView.findViewById(R.id.section_title)
        val sectionContentRecycler : RecyclerView = itemView.findViewById(R.id.content_recyclerview)
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
                    Util.convertDpToPixel(parent.context, 12), 0,
                    Util.convertDpToPixel(parent.context, 4), 0)
            } else if (parent.getChildAdapterPosition(view) == parent.adapter!!.itemCount - 1) {
                outRect.set(
                    Util.convertDpToPixel(parent.context, 4), 0,
                    Util.convertDpToPixel(parent.context, 12), 0)
            } else {
                outRect.set(
                    Util.convertDpToPixel(parent.context, 4), 0,
                    Util.convertDpToPixel(parent.context, 4), 0)
            }
        }
    }
}