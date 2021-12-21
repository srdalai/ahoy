package com.sdtechnocrat.ahoy.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sdtechnocrat.ahoy.data.DownloadItem
import com.sdtechnocrat.ahoy.databinding.ItemDownloadListLayoutBinding

class DownloadsAdapter(val context: Context, private val downloadItems: List<DownloadItem>, val onItemClickListener: OnItemClickListener): RecyclerView.Adapter<DownloadsAdapter.DownloadsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DownloadsViewHolder {
        val binding = ItemDownloadListLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DownloadsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DownloadsViewHolder, position: Int) {
        val item = downloadItems[position]

        Glide.with(context).load(item.contentPoster).into(holder.binding.imageViewPoster)
        holder.binding.tvContentName.text = item.contentName
        holder.binding.tvContentStorage.text = item.downloadedSize.plus(" / ").plus(item.fileSize)

        holder.bind(position, item, onItemClickListener)
    }

    override fun getItemCount(): Int {
        return downloadItems.size
    }

    inner class DownloadsViewHolder(val binding: ItemDownloadListLayoutBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int, item: DownloadItem, listener: OnItemClickListener) {
            binding.imageViewPoster.setOnClickListener {
                listener.onItemClicked(item)
            }
            binding.imageViewPoster.setOnLongClickListener {
                listener.onOptionClicked(position, item)
                true
            }
            binding.imageViewOptions.setOnClickListener {
                listener.onOptionClicked(position, item)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClicked(item: DownloadItem)
        fun onOptionClicked(position: Int, item: DownloadItem)
    }
}