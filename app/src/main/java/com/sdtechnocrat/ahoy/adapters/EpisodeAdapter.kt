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
import com.sdtechnocrat.ahoy.data.EpisodeItem

class EpisodeAdapter(val ctx: Context, val list: List<EpisodeItem>, val onItemClickListener: OnItemClickListener): RecyclerView.Adapter<EpisodeAdapter.EpisodeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpisodeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_episode_list_layout, parent, false)
        return EpisodeViewHolder(view)
    }

    override fun onBindViewHolder(holder: EpisodeViewHolder, position: Int) {
        val episodeItem = list[position]

        Glide.with(ctx).load(episodeItem.poster).into(holder.contentPoster)
        holder.textViewTitle.text = episodeItem.title
        holder.textViewRuntime.text = episodeItem.duration
        holder.textViewStory.text = episodeItem.story

        holder.bind(episodeItem, onItemClickListener)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class EpisodeViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        val contentPoster: ImageView = itemView.findViewById(R.id.content_poster)
        val textViewTitle: TextView = itemView.findViewById(R.id.text_view_title)
        val textViewRuntime: TextView = itemView.findViewById(R.id.text_view_runtime)
        val textViewStory: TextView = itemView.findViewById(R.id.text_view_story)

        fun bind(item: EpisodeItem, listener: OnItemClickListener) {
            itemView.setOnClickListener {
                listener.onItemClicked(item)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClicked(episodeItem: EpisodeItem)
    }
}