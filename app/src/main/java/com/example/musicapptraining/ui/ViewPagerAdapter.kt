package com.example.musicapptraining.ui

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.musicapptraining.ui.fragments.homeFragment.HomeFragment

class ViewPagerAdapter(
    fragmentActivity: HomeFragment,
    private val fragmentList : List<Fragment>,
    private val fragmentNames: List<String>
): FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return fragmentList.size
    }
    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }
}