package com.sdtechnocrat.ahoy.views.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.sdtechnocrat.ahoy.R
import com.sdtechnocrat.ahoy.databinding.FragmentProfileBinding
import com.sdtechnocrat.ahoy.databinding.FragmentSubscriptionBinding
import com.sdtechnocrat.ahoy.utilities.Util.Companion.BASE_URL
import com.sdtechnocrat.ahoy.utilities.Util.Companion.MyPlans
import com.sdtechnocrat.ahoy.views.CommonActivity
import okhttp3.HttpUrl

class SubscriptionFragment : Fragment() {

    private var _binding: FragmentSubscriptionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSubscriptionBinding.inflate(layoutInflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as CommonActivity).setTitle("Subscription")

        binding.paymentHistory.setOnClickListener {
            val fm : FragmentManager = requireActivity().supportFragmentManager
            val ft : FragmentTransaction = fm.beginTransaction();
            ft.replace(R.id.container_frame, PaymentHistoryFragment())
            ft.addToBackStack("payment-history")
            ft.commit()
        }
    }

    private fun getMyPlans() {
        val urlBuilder = HttpUrl.parse(BASE_URL.plus(MyPlans))?.newBuilder()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}