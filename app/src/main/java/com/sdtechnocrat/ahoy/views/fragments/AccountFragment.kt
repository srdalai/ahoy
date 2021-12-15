package com.sdtechnocrat.ahoy.views.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.sdtechnocrat.ahoy.R
import com.sdtechnocrat.ahoy.api.OkHttpBuilder.getOkHttpClient
import com.sdtechnocrat.ahoy.databinding.FragmentAccountBinding
import com.sdtechnocrat.ahoy.utilities.SharedPref
import com.sdtechnocrat.ahoy.utilities.Util.Companion.AUTH_TOKEN
import com.sdtechnocrat.ahoy.utilities.Util.Companion.BASE_URL
import com.sdtechnocrat.ahoy.views.CommonActivity
import com.sdtechnocrat.ahoy.views.MainActivity
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class AccountFragment : Fragment() {

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!

    lateinit var sharedPref: SharedPref

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        sharedPref = SharedPref.getInstance(requireContext())

        _binding = FragmentAccountBinding.inflate(layoutInflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as CommonActivity).setTitle("Account")

        setListeners()

        getProfileDetails()
    }

    private fun setListeners() {
        binding.update.setOnClickListener {
            val fm : FragmentManager = requireActivity().supportFragmentManager
            val ft : FragmentTransaction = fm.beginTransaction();
            ft.replace(R.id.container_frame, UpdateFragment())
            ft.addToBackStack("update-profile")
            ft.commit()
        }
        binding.signOut.setOnClickListener {
            showSignOutDialog("")
        }
    }

    private fun getProfileDetails() {
        binding.progressIndicator.visibility = View.VISIBLE
        val urlBuilder = HttpUrl.parse(BASE_URL.plus("getProfileDetails"))?.newBuilder()
        urlBuilder?.addQueryParameter("authToken", AUTH_TOKEN)
        urlBuilder?.addQueryParameter("email", sharedPref.getUserEmail())
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
                        handleResponse(response.body()?.string()?: "")
                    }
                }
            }
        })
    }

    private fun handleResponse(responseStr: String) {
        val responseObj = JSONObject(responseStr)
        val code = responseObj.optInt("code", 0)
        val message = responseObj.optString("msg", "Something Went Wrong")
        if (code == 200) {
            val name = responseObj.optString("display_name", "")
            val email = responseObj.optString("email", "")
            val profileImage = responseObj.optString("profile_image", "")
            val mobileNumber = responseObj.optString("mobile_number", "")

            Glide.with(requireContext()).load(profileImage).into(binding.imageViewProfile)
            binding.textViewName.text = name
            binding.tvEmail.text = email
            binding.tvMobile.text = mobileNumber

            binding.accountLinear.visibility = View.VISIBLE

        } else {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showSignOutDialog(title: String) {
        val builder = AlertDialog.Builder(requireContext())
        val view = requireActivity().layoutInflater.inflate(R.layout.custom_exit_dialog, null)
        builder.setView(view)
        val dialog = builder.create()

        val textViewTitle: TextView = view.findViewById(R.id.text_view_title)
        val btnCancel: MaterialButton = view.findViewById(R.id.btn_cancel)
        val btnContinue: MaterialButton = view.findViewById(R.id.btn_continue)


        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnContinue.setOnClickListener {
            dialog.dismiss()
            sharedPref.clearAll()
            startActivity(Intent(requireContext(), MainActivity::class.java))
            requireActivity().finishAfterTransition()
        }

        textViewTitle.text = title
        dialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}