package com.sdtechnocrat.ahoy.views.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.sdtechnocrat.ahoy.adapters.PaymentHistoryAdapter
import com.sdtechnocrat.ahoy.api.OkHttpBuilder.getOkHttpClient
import com.sdtechnocrat.ahoy.data.PaymentHistoryItem
import com.sdtechnocrat.ahoy.databinding.FragmentPaymentHistoryBinding
import com.sdtechnocrat.ahoy.utilities.SharedPref
import com.sdtechnocrat.ahoy.utilities.Util.Companion.AUTH_TOKEN
import com.sdtechnocrat.ahoy.utilities.Util.Companion.BASE_URL
import com.sdtechnocrat.ahoy.utilities.Util.Companion.PurchaseHistory
import com.sdtechnocrat.ahoy.views.CommonActivity
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class PaymentHistoryFragment : Fragment() {

    private var _binding: FragmentPaymentHistoryBinding? = null
    val binding get() = _binding!!

    lateinit var sharedPref: SharedPref

    private val paymentList = mutableListOf<PaymentHistoryItem>()
    lateinit var adapter: PaymentHistoryAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        sharedPref = SharedPref.getInstance(requireContext())
        // Inflate the layout for this fragment
        _binding = FragmentPaymentHistoryBinding.inflate(layoutInflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as CommonActivity).setTitle("Payment History")

        val layoutManager = LinearLayoutManager(requireContext())
        adapter = PaymentHistoryAdapter(requireContext(), paymentList)
        binding.paymentRecycler.layoutManager = layoutManager
        binding.paymentRecycler.adapter = adapter

        getPurchaseHistory()
    }

    private fun getPurchaseHistory() {
        binding.progressIndicator.visibility = View.VISIBLE
        val urlBuilder = HttpUrl.parse(BASE_URL.plus(PurchaseHistory))?.newBuilder()
        urlBuilder?.addQueryParameter("authToken", AUTH_TOKEN)
        urlBuilder?.addQueryParameter("user_id", sharedPref.getUserId())
        val url = urlBuilder?.build().toString()
        val request = Request.Builder().url(url).build()
        getOkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                TODO("Not yet implemented")
            }

            override fun onResponse(call: Call, response: Response) {
                requireActivity().runOnUiThread {
                    binding.progressIndicator.visibility = View.GONE
                    if (response.isSuccessful) {
                        handlePurchaseHistoryResponse(response.body()?.string() ?: "")
                    }
                }
            }

        })
    }

    private fun handlePurchaseHistoryResponse(responseStr: String) {
        val responseObj = JSONObject(responseStr)
        val code = responseObj.optInt("code", 0)
        if (code == 200) {
            val paymentArray = responseObj.getJSONArray("section")
            for (i in 0 until paymentArray.length()) {
                val paymentObj = paymentArray.getJSONObject(i)
                val paymentItem = Gson().fromJson(paymentObj.toString(), PaymentHistoryItem::class.java)
                paymentList.add(paymentItem)
            }
            adapter.notifyItemInserted(0)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}