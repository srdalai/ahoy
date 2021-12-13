package com.sdtechnocrat.ahoy.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.sdtechnocrat.ahoy.R
import com.sdtechnocrat.ahoy.data.ProfileActionItem

class ProfileActionAdapter(private val profileActionItems: List<ProfileActionItem>, private val onItemClickListener : OnItemClickListener) : RecyclerView.Adapter<ProfileActionAdapter.ProfileActionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileActionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_profile_action_layout, parent, false)
        return ProfileActionViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProfileActionViewHolder, position: Int) {
        val profileActionItem = profileActionItems[position]

        holder.textView.text = profileActionItem.name
        holder.imageView.setImageDrawable(ContextCompat.getDrawable(holder.imageView.context, profileActionItem.drawable))

        holder.bind(onItemClickListener, profileActionItem)
    }

    override fun getItemCount(): Int {
        return profileActionItems.size
    }

    class ProfileActionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView : ImageView = itemView.findViewById(R.id.imageView)
        val textView : TextView = itemView.findViewById(R.id.textView)

        fun bind(listener: OnItemClickListener, profileActionItem: ProfileActionItem) {
            itemView.setOnClickListener {
                listener.onItemClicked(profileActionItem)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClicked(profileActionItem: ProfileActionItem)
    }
}