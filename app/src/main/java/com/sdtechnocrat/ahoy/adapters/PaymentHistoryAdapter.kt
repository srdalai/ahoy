package com.sdtechnocrat.ahoy.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sdtechnocrat.ahoy.data.PaymentHistoryItem
import com.sdtechnocrat.ahoy.databinding.ItemPaymentHistoryLayoutBinding

class PaymentHistoryAdapter(val content: Context, private val items: List<PaymentHistoryItem>): RecyclerView.Adapter<PaymentHistoryAdapter.PaymentHistoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentHistoryViewHolder {
        val binding = ItemPaymentHistoryLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PaymentHistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PaymentHistoryViewHolder, position: Int) {
        val item = items[position]

        holder.binding.tvTxnDate.text = item.transactionDate
        holder.binding.tvTxnAmount.text = item.currencySymbol.plus(item.amount)
        holder.binding.tvTxnName.text = item.planName
        holder.binding.tvOtherInfo.text = item.planDuration
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class PaymentHistoryViewHolder(val binding: ItemPaymentHistoryLayoutBinding): RecyclerView.ViewHolder(binding.root) {
    }
}