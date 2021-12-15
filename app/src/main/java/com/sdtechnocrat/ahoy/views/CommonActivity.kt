package com.sdtechnocrat.ahoy.views

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.sdtechnocrat.ahoy.R
import com.sdtechnocrat.ahoy.databinding.ActivityCommonBinding
import com.sdtechnocrat.ahoy.utilities.SharedPref
import com.sdtechnocrat.ahoy.views.fragments.AccountFragment
import com.sdtechnocrat.ahoy.views.fragments.AppSettingsFragment
import com.sdtechnocrat.ahoy.views.fragments.DownloadsFragment
import com.sdtechnocrat.ahoy.views.fragments.SubscriptionFragment

class CommonActivity : AppCompatActivity() {

    lateinit var sharedPref: SharedPref

    lateinit var binding: ActivityCommonBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCommonBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        sharedPref = SharedPref.getInstance(this)

        var action: String = intent.getStringExtra("action").toString()

        when(action) {
            ACTION_DOWNLOADS -> {
                changeFragment(DownloadsFragment())
            }
            ACTION_ACCOUNT -> {
                changeFragment(AccountFragment())
            }
            ACTION_SUB -> {
                changeFragment(SubscriptionFragment())
            }
            ACTION_SETTINGS -> {
                changeFragment(AppSettingsFragment())
            }
        }

        binding.imageViewBack.setOnClickListener {
            super.onBackPressed()
        }
    }

    private fun changeFragment(fragment: Fragment) {
        val fm : FragmentManager = supportFragmentManager
        val ft : FragmentTransaction = fm.beginTransaction();
        ft.replace(R.id.container_frame, fragment)
        ft.commit()
    }

    fun setTitle(title: String) {
        binding.textViewTitle.text = title
    }

    companion object {
        const val ACTION_DOWNLOADS = "downloads"
        const val ACTION_ACCOUNT = "account"
        const val ACTION_SUB = "subscription"
        const val ACTION_SETTINGS = "settings"
    }
}