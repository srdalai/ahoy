package com.sdtechnocrat.ahoy.adapters

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import com.sdtechnocrat.ahoy.R
import com.sdtechnocrat.ahoy.data.BannerItem

class PagerIndicatorAdapter(val context: Context, private val items: List<BannerItem>, private val totalWidth : Int, private var current : Int) : RecyclerView.Adapter<PagerIndicatorAdapter.PagerIndicatorViewHolder>() {


    lateinit var currentIndicator : View

    constructor(context: Context, items: List<BannerItem>, totalWidth : Int) : this(context, items, totalWidth, 0) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerIndicatorViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pager_indicator_layout, parent, false)
        return PagerIndicatorViewHolder(view);
    }

    override fun onBindViewHolder(holder: PagerIndicatorViewHolder, position: Int) {
        val bannerItem = items[position]
        var layoutParams = holder.indicatorFrame.layoutParams as FrameLayout.LayoutParams
        layoutParams.width = (totalWidth/items.size) - 34
        holder.indicatorFrame.layoutParams = layoutParams

        holder.indicatorFrame.isSelected = position == current

        holder.indicatorFrame.background.level = 0

        if (position == current) {
            currentIndicator = holder.indicatorFrame
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun setCurrentItem(current : Int) {
        Log.d("TAG", current.toString())
        this.current = current
        notifyItemRangeChanged(0, items.size)
    }

    fun setPercentage(percent : Int) {
        currentIndicator.background.level = (100 * percent)
    }



    class PagerIndicatorViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val indicatorFrame : FrameLayout = itemView.findViewById(R.id.indicator_frame)
    }
}