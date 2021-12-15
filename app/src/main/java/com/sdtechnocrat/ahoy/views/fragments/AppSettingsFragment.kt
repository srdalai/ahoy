package com.sdtechnocrat.ahoy.views.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sdtechnocrat.ahoy.R
import com.sdtechnocrat.ahoy.databinding.FragmentAppSettingsBinding
import com.sdtechnocrat.ahoy.databinding.FragmentSubscriptionBinding
import com.sdtechnocrat.ahoy.views.CommonActivity

class AppSettingsFragment : Fragment() {

    private var _binding: FragmentAppSettingsBinding? = null
    val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentAppSettingsBinding.inflate(layoutInflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as CommonActivity).setTitle("App Settings")
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}