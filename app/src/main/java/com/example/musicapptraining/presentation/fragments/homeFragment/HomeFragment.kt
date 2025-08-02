package com.example.musicapptraining.presentation.fragments.homeFragment

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.core.net.toUri
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.musicapptraining.R
import com.example.musicapptraining.presentation.ViewPagerAdapter
import com.example.musicapptraining.databinding.FragmentHomeBinding
import com.example.musicapptraining.domain.model.Song
import com.example.musicapptraining.presentation.musicPlayer.PlaybackViewModel
import com.example.musicapptraining.presentation.fragments.artistFragment.ArtistFragment
import com.example.musicapptraining.presentation.fragments.baseFragment.BaseFragment
import com.example.musicapptraining.presentation.fragments.playlistFragment.PlaylistFragment
import com.example.musicapptraining.presentation.fragments.songsFragment.SongsFragment
import com.example.musicapptraining.presentation.bottomSheetFragments.playedSongBottomSheet.PlayedSongBottomSheet
import com.example.musicapptraining.utilities.PlayerEvents
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private val playerViewModel : PlaybackViewModel by activityViewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewPagerAdapterForFragments()
        setViewModelObservers()
        setCLickListeners()
    }
    private fun setViewPagerAdapterForFragments(){
        val fragmentList = listOf(
            SongsFragment() to SONGS,
            ArtistFragment() to ARTISTS,
            PlaylistFragment() to PLAYLISTS
        )
        val fragmentInstances = fragmentList.map { it.first }
        val fragmentTitles = fragmentList.map { it.second }
        viewPagerAdapter = ViewPagerAdapter(
            this@HomeFragment,
            fragmentInstances,
            fragmentTitles
        )
        binding.vpViewPager.adapter = viewPagerAdapter
        TabLayoutMediator(binding.tlButtons,binding.vpViewPager){tab, position->
            tab.text = fragmentTitles[position]
        }.attach()
    }
    private fun setViewModelObservers(){
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                launch {
                    setCurrentSongObserver()
                }
                launch {
                    setIsPausePlayClickedObserver()
                }
            }
        }
    }
    private suspend fun setCurrentSongObserver(){
        playerViewModel.currentSong.collect{song->
            binding.apply {
                tvSongName.text = song.songName
                tvArtistName.text = song.songArtist
                ivSongImage.setImageURI(song.songArt?.toUri())
            }
        }
    }
    private suspend fun setIsPausePlayClickedObserver(){
        playerViewModel.isPlaying.collect{state->
            setCorrectImageBasedOnIsPausePlayClickedValue(state)
        }
    }
    private fun setCorrectImageBasedOnIsPausePlayClickedValue(state:Boolean){
        if (state){
            binding.ivPlayPause.setImageResource(R.drawable.pause_svgrepo_com)
        }else{
            binding.ivPlayPause.setImageResource(R.drawable.play_svgrepo_com)
        }
    }
    private fun setCLickListeners(){
        binding.apply {
            tvSongName.setOnClickListener {
                playerViewModel.getEvent(
                    PlayerEvents.GetThePositionOfSpecificSongInsideThePlayList(
                        playerViewModel.currentSong.value.songId
                    )
                )
                showPlayedSongBottomSheet(playerViewModel.currentSong.value)
            }
            ibMore.setOnClickListener {
                showMenuForMoreOptions(it)
            }
            ibSearch.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
            }
            ivNextSong.setOnClickListener {
                playerViewModel.getEvent(PlayerEvents.Next)
            }
            ivPlayPause.setOnClickListener {
                playerViewModel.getEvent(PlayerEvents.PausePlay)
            }
        }
    }
    private fun showMenuForMoreOptions(view : View){
        val popupMenu = PopupMenu(context, view)
        popupMenu.menuInflater.inflate(R.menu.menu_items, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener {item->
            setCorrectOrderForEveryItemId(item)
        }
        popupMenu.show()
    }
    private fun setCorrectOrderForEveryItemId(item: MenuItem):Boolean{
        return when(item.itemId){
             R.id.find_local_songs -> {
                 findNavController().navigate(
                     R.id.action_homeFragment_to_scanLocalAudiosFromDeviceFragment
                 )
                 true
             }
             R.id.settings ->{
                 true
             }
             else -> false
         }
    }
    private fun showPlayedSongBottomSheet(song: Song){
        val bottomSheet = PlayedSongBottomSheet.newInstance(song)
        bottomSheet.show(parentFragmentManager,tag)
    }
    companion object{
        const val SONGS = "Songs"
        const val ARTISTS = "Artists"
        const val PLAYLISTS = "Playlists"
    }
}