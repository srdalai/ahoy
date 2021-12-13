package com.sdtechnocrat.ahoy.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.sdtechnocrat.ahoy.R
import com.sdtechnocrat.ahoy.data.CategoryItem

class CategoryAdapter(val list: List<CategoryItem>, val onItemClickListener: OnItemClickListener): RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    var currentSelected = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category_list_layout, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val item = list[position]
        holder.textView.text = item.name

        if (position == currentSelected) {
            holder.textView.isActivated = true
            val typeface = ResourcesCompat.getFont(holder.textView.context, R.font.sf_ui_text_bold)
            holder.textView.typeface = typeface
        } else {
            holder.textView.isActivated = false
            val typeface = ResourcesCompat.getFont(holder.textView.context, R.font.sf_ui_text_regular)
            holder.textView.typeface = typeface
        }

        holder.bind(position, item, onItemClickListener)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setSelected(pos: Int) {
        currentSelected = pos
        notifyDataSetChanged()
    }

    class CategoryViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val textView : TextView = itemView.findViewById(R.id.textView)

        fun bind(position: Int, categoryItem: CategoryItem, listener: OnItemClickListener) {
            itemView.setOnClickListener {
                listener.onItemClicked(position, categoryItem)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClicked(position: Int, categoryItem: CategoryItem)
    }
}