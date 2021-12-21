package com.sdtechnocrat.ahoy.views.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.sdtechnocrat.ahoy.R
import com.sdtechnocrat.ahoy.api.OkHttpBuilder.getOkHttpClient
import com.sdtechnocrat.ahoy.databinding.FragmentProfileBinding
import com.sdtechnocrat.ahoy.databinding.FragmentSubscriptionBinding
import com.sdtechnocrat.ahoy.utilities.SharedPref
import com.sdtechnocrat.ahoy.utilities.Util.Companion.AUTH_TOKEN
import com.sdtechnocrat.ahoy.utilities.Util.Companion.BASE_URL
import com.sdtechnocrat.ahoy.utilities.Util.Companion.MyPlans
import com.sdtechnocrat.ahoy.views.CommonActivity
import com.sdtechnocrat.ahoy.views.PlanSelectionActivity
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class SubscriptionFragment : Fragment() {

    private var _binding: FragmentSubscriptionBinding? = null
    private val binding get() = _binding!!

    lateinit var sharedPref: SharedPref

    lateinit var planId: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSubscriptionBinding.inflate(layoutInflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as CommonActivity).setTitle("Subscription")

        sharedPref = SharedPref.getInstance(requireContext())

        binding.paymentHistory.setOnClickListener {
            val fm : FragmentManager = requireActivity().supportFragmentManager
            val ft : FragmentTransaction = fm.beginTransaction();
            ft.replace(R.id.container_frame, PaymentHistoryFragment())
            ft.addToBackStack("payment-history")
            ft.commit()
        }

        binding.btnUpgrade.setOnClickListener {
            val intent = Intent(requireContext(), PlanSelectionActivity::class.java).apply {
                //putExtra("is_upgrade", true)
                putExtra("current_plan_id", planId)
            }
            startActivity(intent)
        }

        getMyPlans()
    }

    private fun getMyPlans() {
        binding.progressIndicator.visibility = View.VISIBLE
        val urlBuilder = HttpUrl.parse(BASE_URL.plus(MyPlans))?.newBuilder()
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
                        handlePlansResponse(response.body()?.string() ?: "")
                    }
                }
            }

        })

    }

    private fun handlePlansResponse(responseStr: String) {
        val responseObj = JSONObject(responseStr)
        val code = responseObj.optInt("code", 0)
        if (code == 200) {
            val plansArray = responseObj.getJSONArray("Plan")
            if (plansArray.length() > 0) {
                val planObj = plansArray.getJSONObject(0)
                val planName = planObj.optString("name", "")
                val endDate = planObj.optString("end_date", "")
                planId = planObj.optString("plan_id", "")


                binding.bigPlanName.text = planName
                binding.tvSubPlan.text = planName
                binding.bigPlanExpiry.text = "Expires on ".plus(formatDate(endDate))
                binding.tvNextRenewal.text = formatDate(endDate)

                binding.subLinear.visibility = View.VISIBLE
            }
        }
    }

    private fun formatDate(dateStr: String): String? {
        val inputDf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val outputDf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        try {
            val parsedDate = inputDf.parse(dateStr)
            return outputDf.format(parsedDate)
        } catch (e: Exception) {
        }
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}