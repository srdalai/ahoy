package com.sdtechnocrat.ahoy.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sdtechnocrat.ahoy.data.PlanItem
import com.sdtechnocrat.ahoy.databinding.ItemPlanLayoutBinding

class PlansAdapter(val context: Context, private val planList: List<PlanItem>, val activePosition: Int, val onItemClickListener: OnItemClickListener): RecyclerView.Adapter<PlansAdapter.PlanViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanViewHolder {
        val binding = ItemPlanLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlanViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlanViewHolder, position: Int) {
        val planItem = planList[position]
        val currency = planItem.currency!!

        holder.binding.tvPlanName.text = planItem.name

        val planPrice = currency.symbol.plus(planItem.price).plus("/").plus(planItem.frequency).plus(" ").plus(planItem.recurrence)

        holder.binding.tvPrice.text = planPrice

        holder.binding.parentFrame.isActivated = position == activePosition

        holder.bind(position, planItem, onItemClickListener)
    }

    override fun getItemCount(): Int {
        return planList.size
    }

    inner class PlanViewHolder(val binding: ItemPlanLayoutBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int, item: PlanItem, listener: OnItemClickListener) {
            binding.btnSubscribe.setOnClickListener {
                listener.itemClicked(position, item)
            }
        }
    }

    interface OnItemClickListener {
        fun itemClicked(position: Int, planItem: PlanItem)
    }
}