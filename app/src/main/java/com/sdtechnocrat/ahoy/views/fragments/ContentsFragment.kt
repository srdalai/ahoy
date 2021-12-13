package com.sdtechnocrat.ahoy.views.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sdtechnocrat.ahoy.R
import com.sdtechnocrat.ahoy.databinding.FragmentCategoryBinding
import com.sdtechnocrat.ahoy.databinding.FragmentContentsBinding

class ContentsFragment : Fragment() {

    private var _binding: FragmentContentsBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentContentsBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}