package com.sdtechnocrat.ahoy.views.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.sdtechnocrat.ahoy.R
import com.sdtechnocrat.ahoy.databinding.FragmentSliderPageBinding


class SliderPageFragment : Fragment() {

    private var _binding : FragmentSliderPageBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSliderPageBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bundle = arguments
        val banner_url = bundle?.get("banner_url")
        Glide.with(this).load(banner_url).into(binding.imageView)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}