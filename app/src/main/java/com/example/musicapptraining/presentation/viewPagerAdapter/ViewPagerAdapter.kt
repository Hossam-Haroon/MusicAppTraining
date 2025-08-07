package com.example.musicapptraining.presentation.viewPagerAdapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.musicapptraining.presentation.fragments.artistFragment.ArtistFragment
import com.example.musicapptraining.presentation.fragments.homeFragment.HomeFragment
import com.example.musicapptraining.presentation.fragments.playlistFragment.PlaylistFragment
import com.example.musicapptraining.presentation.fragments.songsFragment.SongsFragment

class ViewPagerAdapter(
    fragmentActivity: HomeFragment,
    private val fragmentNames: List<String>
): FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return fragmentNames.size
    }
    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> SongsFragment()
            1 -> ArtistFragment()
            2 -> PlaylistFragment()
            else -> throw IllegalArgumentException("invalid position: $position")
        }
    }
}