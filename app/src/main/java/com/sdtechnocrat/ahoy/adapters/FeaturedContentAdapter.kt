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
import com.sdtechnocrat.ahoy.data.FeaturedContentItem

class FeaturedContentAdapter(val context: Context, private val itemList: List<FeaturedContentItem?>?, private val onItemClickListener: OnItemClickListener) : RecyclerView.Adapter<FeaturedContentAdapter.FeaturedContentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeaturedContentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_content_card_list_layout, parent, false)
        return FeaturedContentViewHolder(view)
    }

    override fun onBindViewHolder(holder: FeaturedContentViewHolder, position: Int) {
        val contentItem = itemList?.get(position)
        val title = contentItem?.name

        holder.grayscaleTitle.text = title
        if (contentItem?.posterForTv?.length!! > 0) {
            holder.grayscaleTitle.visibility = View.GONE
            Glide.with(context).load(contentItem.posterForTv).into(holder.contentPoster)
        } else {
            //Glide.with(context).load(R.color.color_accent).into(holder.contentPoster)
        }

        holder.bind(contentItem, onItemClickListener)

    }

    override fun getItemCount(): Int {
        return itemList?.size ?: 0
    }

    class FeaturedContentViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val contentPoster : ImageView = itemView.findViewById(R.id.content_poster)
        val grayscaleTitle : TextView = itemView.findViewById(R.id.grayscale_title)

        fun bind(contentItem: FeaturedContentItem, listener: OnItemClickListener) {
            itemView.setOnClickListener {
                listener.onItemClicked(contentItem)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClicked(contentItem: FeaturedContentItem)
    }
}