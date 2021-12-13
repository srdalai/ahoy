package com.sdtechnocrat.ahoy.adapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.sdtechnocrat.ahoy.data.ContentItem
import com.sdtechnocrat.ahoy.views.fragments.EpisodesFragment
import com.sdtechnocrat.ahoy.views.fragments.SimilarFragment

class DetailsPagerAdapter(fragmentActivity: FragmentActivity, private val contentItem: ContentItem): FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        if (contentItem.contentTypesId == 3) return 2
        else return 1
    }

    override fun createFragment(position: Int): Fragment {
        if (contentItem.contentTypesId == 3) {
            if (position == 0) {
                return EpisodesFragment()
            } else {
                val similarFrag = SimilarFragment()
                similarFrag.arguments = Bundle().apply {
                    putString("content_id", contentItem.contentId)
                    putString("content_stream_id", contentItem.contentStreamId)
                }
                return similarFrag
            }

        } else {
            val similarFrag = SimilarFragment()
            similarFrag.arguments = Bundle().apply {
                putString("content_id", contentItem.contentId)
                putString("content_stream_id", contentItem.contentStreamId)
            }
            return similarFrag
        }
    }
}