package com.sdtechnocrat.ahoy.views

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.sdtechnocrat.ahoy.api.OkHttpBuilder.getOkHttpClient
import com.sdtechnocrat.ahoy.databinding.ActivityLoginBinding
import com.sdtechnocrat.ahoy.utilities.SharedPref
import com.sdtechnocrat.ahoy.utilities.Util.Companion.AUTH_TOKEN
import com.sdtechnocrat.ahoy.utilities.Util.Companion.BASE_URL
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class LoginActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLoginBinding

    private lateinit var sharedPref: SharedPref

    private lateinit var emailStr : String
    private lateinit var passwordStr : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        sharedPref = SharedPref.getInstance(this)

        binding.btnLogin.setOnClickListener {
            emailStr = binding.edittextEmail.text.toString()
            passwordStr = binding.edittextPassword.text.toString()

            logIn()
        }

        binding.edittextEmail.setText("freeuser@yahoo.com")
        binding.edittextPassword.setText("freeuser123")
    }

    private fun logIn() {
        binding.progressIndicator.visibility = View.VISIBLE
        val url = BASE_URL.plus("LoginV1")

        val formBody = FormBody.Builder()
            .add("authToken", AUTH_TOKEN)
            .add("email", emailStr)
            .add("password", passwordStr)
            .build()

        val request  = Request.Builder()
            .url(url)
            .post(formBody)
            .build()

        getOkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                TODO("Not yet implemented")
            }

            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    binding.progressIndicator.visibility = View.GONE
                    if (response.isSuccessful) {
                        handleLoginResponse(response.body()?.string()?: "")
                    }
                }
            }

        })
    }

    private fun handleLoginResponse(responseStr: String) {
        val responseObj  = JSONObject(responseStr)
        val code = responseObj.optInt("code", 0)
        val message = responseObj.optString("msg", "Error")
        if (code == 200) {
            val userId = responseObj.optString("id")
            val isFreeUser = responseObj.optString("isFree")
            val email = responseObj.optString("email")
            val name = responseObj.optString("display_name")
            val loginHistoryId = responseObj.optString("login_history_id")
            val isSubscribed = responseObj.optInt("isSubscribed", 0).toString()
            val profileImageUrl = responseObj.optString("profile_image")
            val kidsModeStatus = responseObj.optInt("kids_mode_status", 0)

            sharedPref.setUserId(userId)
            sharedPref.setUserEmail(email)
            sharedPref.setUserImage(profileImageUrl)
            sharedPref.setUserName(name)

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finishAfterTransition()
        }

    }
}