package com.sdtechnocrat.ahoy.data

import com.google.gson.annotations.SerializedName

data class PaymentHistoryItem(

	@field:SerializedName("transaction_date")
	val transactionDate: String? = null,

	@field:SerializedName("Content_type")
	val contentType: String? = null,

	@field:SerializedName("amount")
	val amount: String? = null,

	@field:SerializedName("transaction_status")
	val transactionStatus: String? = null,

	@field:SerializedName("transaction_for")
	val transactionFor: String? = null,

	@field:SerializedName("currency_symbol")
	val currencySymbol: String? = null,

	@field:SerializedName("content_uniq_id")
	val contentUniqId: String? = null,

	@field:SerializedName("movie_name")
	val movieName: String? = null,

	@field:SerializedName("currency_code")
	val currencyCode: String? = null,

	@field:SerializedName("plan_name")
	val planName: String? = null,

	@field:SerializedName("plan_duration")
	val planDuration: String? = null,

	@field:SerializedName("stream_uniq_id")
	val streamUniqId: String? = null,

	@field:SerializedName("invoice_id")
	val invoiceId: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("plan_id")
	val planId: String? = null,

	@field:SerializedName("statusppv")
	val statusPpv: String? = null
)
