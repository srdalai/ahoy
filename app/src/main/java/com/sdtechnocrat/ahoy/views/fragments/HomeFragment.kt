package com.sdtechnocrat.ahoy.views.fragments

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.gson.Gson
import com.sdtechnocrat.ahoy.adapters.FeaturedSectionAdapter
import com.sdtechnocrat.ahoy.adapters.PagerIndicatorAdapter
import com.sdtechnocrat.ahoy.api.MuviApiService
import com.sdtechnocrat.ahoy.api.ServiceBuilder
import com.sdtechnocrat.ahoy.data.BannerItem
import com.sdtechnocrat.ahoy.data.FeaturedContentResponse
import com.sdtechnocrat.ahoy.databinding.FragmentHomeBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class HomeFragment : Fragment() {

    private val TAG = "HomeFragment"

    private var _binding : FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var currentPage : Int = 0
    private var bannerItems : List<BannerItem> = listOf()
    private lateinit var indicatorAdapter : PagerIndicatorAdapter

    val update = Runnable {
        if (currentPage == bannerItems.size - 1) {
            currentPage = 0
        } else {
            currentPage++
        }
        changeBannerItem()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainHandler = Handler(Looper.getMainLooper())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    var _update : Boolean = true
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*val totalWidth = binding.pagerIndicator.width
        indicatorAdapter = PagerIndicatorAdapter(requireContext(), bannerItems, totalWidth, 0)
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.pagerIndicator.layoutManager = layoutManager
        binding.pagerIndicator.adapter = indicatorAdapter*/

        binding.pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                //Log.d(TAG, "Selected $position")
                currentPage = position
                percent = 0
                indicatorAdapter.setCurrentItem(position)
            }
        })

        getFeaturedContents()
    }

    private fun getFeaturedContents() {
        binding.progressIndicator.visibility = View.VISIBLE
        val request = ServiceBuilder.buildService(MuviApiService::class.java)
        val call = request.getAppHomeFeature("92b9a7907748d038f3277feafcf07506", "1", "10", "1", "5", "tv");
        call.enqueue(object : Callback<FeaturedContentResponse> {
            override fun onResponse(call: Call<FeaturedContentResponse>, response: Response<FeaturedContentResponse>) {
                val featuredContentDetails = response.body()
                handleHomeFeatured(featuredContentDetails)
            }

            override fun onFailure(call: Call<FeaturedContentResponse>, t: Throwable) {
                Log.e(TAG, t.toString())
            }

        })
    }

    private fun handleHomeFeatured(homeFeatured: FeaturedContentResponse?) {
        binding.progressIndicator.visibility = View.GONE
        Log.d(TAG, "Response: =>\n" + Gson().toJson(homeFeatured))
        if (homeFeatured?.bannerList?.size!! > 0) {
            bannerItems = homeFeatured.bannerList as List<BannerItem>
            val pagerAdapter = ScreenSlidePagerAdapter(this.requireActivity(),
                bannerItems
            )
            binding.pager.adapter = pagerAdapter

            val totalWidth = binding.pagerIndicator.width
            indicatorAdapter = PagerIndicatorAdapter(requireContext(), bannerItems, totalWidth)
            val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            binding.pagerIndicator.layoutManager = layoutManager
            binding.pagerIndicator.adapter = indicatorAdapter

            mainHandler.post(updateIndicatorTask)
        }

        if (homeFeatured.featuredSections?.size!! > 0) {
            val layoutManager = LinearLayoutManager(requireContext())
            val sectionAdapter = FeaturedSectionAdapter(requireActivity(), homeFeatured.featuredSections)
            binding.sectionRecycler.layoutManager = layoutManager
            binding.sectionRecycler.adapter = sectionAdapter
        }
    }

    private fun changeBannerItem() {
        binding.pager.setCurrentItem(currentPage, true)
    }

    lateinit var mainHandler: Handler
    var percent = 0
    private val updateIndicatorTask = object : Runnable {
        override fun run() {
            plusOneSecond()
            mainHandler.postDelayed(this, 100)
        }
    }

    fun plusOneSecond() {
        if (percent <= 100) {
            indicatorAdapter.setPercentage(percent)
            percent += 5
        } else if (currentPage < bannerItems.size) {
            //Log.d(TAG, "Change Banner")
            //_update = false
            currentPage++
            percent = 0
            changeBannerItem()
            //animateViewPager(binding.pager, binding.pager.width, 1000);
        }
    }

    private var animFactor = 0
    private val animator = ValueAnimator()

    private fun animateViewPager(pager: ViewPager2, offset: Int, delay: Int) {
        if (!animator.isRunning) {
            animator.removeAllUpdateListeners()
            animator.removeAllListeners()
            //Set animation
            animator.setIntValues(0, -offset)
            animator.duration = delay.toLong()
            animator.repeatCount = 1
            animator.repeatMode = ValueAnimator.RESTART
            animator.addUpdateListener { animation ->
                val value = animFactor * animation.animatedValue as Int
                if (!pager.isFakeDragging) {
                    pager.beginFakeDrag()
                }
                pager.fakeDragBy(value.toFloat())
            }
            animator.addListener(object: Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                    animFactor = 1
                }

                override fun onAnimationEnd(animation: Animator) {
                    pager.endFakeDrag()
                }

                override fun onAnimationCancel(animation: Animator) {

                }

                override fun onAnimationRepeat(animation: Animator) {
                    animFactor = -1
                }

            })
            animator.start()
        }
    }

    override fun onPause() {
        super.onPause()
        mainHandler.removeCallbacks(updateIndicatorTask)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private inner class ScreenSlidePagerAdapter(fa: FragmentActivity, val banners: List<BannerItem?>) : FragmentStateAdapter(fa) {

        override fun getItemCount(): Int {
            return banners.size
        }

        override fun createFragment(position: Int): Fragment {
            var frag = SliderPageFragment()
            var bundle = Bundle()
            bundle.putString("banner_url", banners.get(position)?.imagePath)
            frag.arguments = bundle
            return frag
        }

    }
}