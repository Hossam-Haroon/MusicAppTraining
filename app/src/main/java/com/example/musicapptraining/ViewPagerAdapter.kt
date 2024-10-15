package com.example.musicapptraining

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.musicapptraining.ui.homeFragment.HomeFragment

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