package com.sdtechnocrat.ahoy.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.sdtechnocrat.ahoy.R
import com.sdtechnocrat.ahoy.adapters.PlansAdapter
import com.sdtechnocrat.ahoy.api.OkHttpBuilder.getOkHttpClient
import com.sdtechnocrat.ahoy.data.PlanItem
import com.sdtechnocrat.ahoy.databinding.ActivityPlanSelectionBinding
import com.sdtechnocrat.ahoy.utilities.Util.Companion.AUTH_TOKEN
import com.sdtechnocrat.ahoy.utilities.Util.Companion.BASE_URL
import com.sdtechnocrat.ahoy.utilities.Util.Companion.getStudioPlanLists
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class PlanSelectionActivity : AppCompatActivity() {

    lateinit var binding: ActivityPlanSelectionBinding

    private val planList = mutableListOf<PlanItem>()
    lateinit var adapter: PlansAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlanSelectionBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.planRecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        adapter = PlansAdapter(this, planList, 0, object : PlansAdapter.OnItemClickListener {
            override fun itemClicked(position: Int, planItem: PlanItem) {
                Toast.makeText(this@PlanSelectionActivity, planItem.name, Toast.LENGTH_SHORT).show()
            }
        })
        binding.planRecycler.adapter = adapter

        getStudioPlans()
    }

    private fun getStudioPlans() {
        binding.progressIndicator.visibility = View.VISIBLE
        val urlBuilder = HttpUrl.parse(BASE_URL.plus(getStudioPlanLists))?.newBuilder()
        urlBuilder?.addQueryParameter("authToken", AUTH_TOKEN)
        val url = urlBuilder?.build().toString()
        val request = Request.Builder().url(url).build()
        getOkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                TODO("Not yet implemented")
            }

            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
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
        if (code == 200) {
            val plansArray = responseObj.getJSONArray("plans")
            for (i in 0 until plansArray.length()) {
                val planObj = plansArray.getJSONObject(i)
                val item = Gson().fromJson(planObj.toString(), PlanItem::class.java)
                planList.add(item)
            }
            adapter.notifyItemInserted(0)
        }
    }
}