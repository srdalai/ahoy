package com.sdtechnocrat.ahoy.views

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.sdtechnocrat.ahoy.R
import com.sdtechnocrat.ahoy.databinding.ActivityMainBinding
import com.sdtechnocrat.ahoy.views.fragments.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    lateinit var currentFragment : Fragment

    private val SPEECH_REQUEST_CODE = 101


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root;
        setContentView(view)

        setUpListeners()

        changeFragment(HomeFragment())
    }

    private fun setUpListeners() {
        binding.menuHome.setOnClickListener {
            changeFragment(HomeFragment())
        }

        binding.menuSearch.setOnClickListener {
            changeFragment(SearchFragment())
        }

        binding.menuCategory.setOnClickListener {
            changeFragment(CategoryFragment())
        }

        binding.menuProfile.setOnClickListener {
            changeFragment(ProfileFragment())
        }
    }

    private fun changeFragment(fragment: Fragment) {
        val fm : FragmentManager = supportFragmentManager
        val ft : FragmentTransaction = fm.beginTransaction();
        ft.replace(R.id.container, fragment)
        ft.commit()
        currentFragment = fragment
    }

    // Create an intent that can start the Speech Recognizer activity
    fun displaySpeechRecognizer() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        }
        // This starts the activity and populates the intent with the speech text.
        startActivityForResult(intent, SPEECH_REQUEST_CODE)
    }

    // This callback is invoked when the Speech Recognizer returns.
    // This is where you process the intent and extract the speech text from the intent.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val spokenText: String =
                data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).let { results ->
                    results?.get(0) ?: ""
                }
            // Do something with spokenText.
            //Toast.makeText(this, spokenText, Toast.LENGTH_SHORT).show()
            if (spokenText.isNotEmpty()) {
                (currentFragment as SearchFragment).searchByVoice(spokenText)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onBackPressed() {
        if (currentFragment is HomeFragment) {
            showExitDialog()
        } else {
            changeFragment(HomeFragment())
        }
    }

    private fun showExitDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Exit App?")
        builder.setPositiveButton("Yes") { dialog, _ ->
            dialog?.dismiss()
            finishAffinity()
        }
        builder.setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
        builder.create().show()
    }
}