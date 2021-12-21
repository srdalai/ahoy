package com.sdtechnocrat.ahoy.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.sdtechnocrat.ahoy.R
import com.sdtechnocrat.ahoy.databinding.ActivityPreLoginBinding

class PreLoginActivity : AppCompatActivity() {

    lateinit var binding : ActivityPreLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPreLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun changeFragment(fragment: Fragment) {
        val fm : FragmentManager = supportFragmentManager
        val ft : FragmentTransaction = fm.beginTransaction();
        ft.replace(R.id.account_frame, fragment)
        ft.commit()
    }
}