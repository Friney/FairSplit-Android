package com.friney.fairsplit.ui.details.event

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class DetailsEventPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ReceiptsTabFragment()
            1 -> SummaryTabFragment()
            else -> throw IllegalArgumentException("Invalid position $position")
        }
    }
} 