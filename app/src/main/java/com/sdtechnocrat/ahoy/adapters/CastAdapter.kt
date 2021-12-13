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
import com.sdtechnocrat.ahoy.data.CastDetailItem

class CastAdapter(private val context: Context, private val castList: List<CastDetailItem>, private val onItemClickListener: OnItemClickListener) : RecyclerView.Adapter<CastAdapter.CastViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CastViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cast_grid_layout, parent, false)
        return CastViewHolder(view)
    }

    override fun onBindViewHolder(holder: CastViewHolder, position: Int) {
        val castItem = castList[position]
        holder.textView.text = castItem.celebName
        Glide.with(context).load(castItem.celebImage).into(holder.imageView)

        holder.bind(castItem, onItemClickListener)
    }

    override fun getItemCount(): Int {
        return castList.size
    }

    class CastViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val textView: TextView = itemView.findViewById(R.id.textView)

        fun bind(castItem: CastDetailItem, listener: OnItemClickListener) {
            itemView.setOnClickListener {
                listener.onItemClicked(castItem)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClicked(castDetailItem: CastDetailItem)
    }
}