package com.sdtechnocrat.ahoy.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sdtechnocrat.ahoy.R
import com.sdtechnocrat.ahoy.data.ContentItem

class ContentGridAdapter(private val context: Context, private val contentList: List<ContentItem>, private val onItemClickListener: OnItemClickListener) : RecyclerView.Adapter<ContentGridAdapter.ContentGridViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentGridViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_content_card_grid_layout, parent, false)
        return ContentGridViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContentGridViewHolder, position: Int) {
        val contentItem = contentList[position]
        val title = contentItem.title

        holder.grayscaleTitle.text = title
        if (contentItem.poster.isNotEmpty()) {
            holder.grayscaleTitle.visibility = View.GONE
            Glide.with(context).load(contentItem.poster).into(holder.contentPoster)
        }

        holder.bind(contentItem, onItemClickListener)
    }

    override fun getItemCount(): Int {
        return contentList.size
    }

    class ContentGridViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val contentPoster : ImageView = itemView.findViewById(R.id.content_poster)
        val grayscaleTitle : TextView = itemView.findViewById(R.id.grayscale_title)

        fun bind(contentItem: ContentItem, listener: OnItemClickListener) {
            itemView.setOnClickListener {
                listener.onItemClicked(contentItem)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClicked(contentItem: ContentItem)
    }
}