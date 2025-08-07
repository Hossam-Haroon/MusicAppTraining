package com.example.musicapptraining.presentation.fragments.homeFragment

import android.os.Bundle
import android.view.View
import androidx.core.net.toUri
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.musicapptraining.R
import com.example.musicapptraining.presentation.viewPagerAdapter.ViewPagerAdapter
import com.example.musicapptraining.databinding.FragmentHomeBinding
import com.example.musicapptraining.utilities.PlayedSongBottomSheetHandler
import com.example.musicapptraining.domain.model.Song
import com.example.musicapptraining.presentation.fragments.baseFragment.BaseFragment
import com.example.musicapptraining.presentation.bottomSheetFragments.playedSongBottomSheet.PlayedSongBottomSheet
import com.example.musicapptraining.presentation.PlayerControllerViewModel.PlayerControllerViewModel
import com.example.musicapptraining.utilities.PlayerEvents
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment :
    BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate),
    PlayedSongBottomSheetHandler {
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private val playerViewModel : PlayerControllerViewModel by activityViewModels()
    private lateinit var homeFragmentUiListener : HomeFragmentUiListener
    private var wasPlayingBeforeNavigation = false
    override fun onPause() {
        super.onPause()
        wasPlayingBeforeNavigation = playerViewModel.playbackState.value.isPlaying
    }
    override fun onResume() {
        super.onResume()
        if (wasPlayingBeforeNavigation && playerViewModel.playbackState.value.isPlaying){
            lifecycleScope.launch {
                delay(100)
                playerViewModel.getEvent(PlayerEvents.PausePlay)
            }
        }
        wasPlayingBeforeNavigation = false
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeFragmentUiListener = HomeFragmentUiListener(
            binding,
            findNavController(),
            playerViewModel,
            this,
            requireContext()
        )
        setViewPagerAdapterForFragments()
        setViewModelObservers()
        homeFragmentUiListener.setCLickListeners()
        //playerViewModel.checkServiceConnection()
    }
    private fun setViewPagerAdapterForFragments(){
        val fragmentTitles = listOf(SONGS,ARTISTS,PLAYLISTS)
        viewPagerAdapter = ViewPagerAdapter(this@HomeFragment, fragmentTitles)
        binding.vpViewPager.apply {
            adapter = viewPagerAdapter
            offscreenPageLimit = 3
        }
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
        playerViewModel.playbackState.collect{state->
            setCorrectImageBasedOnIsPausePlayClickedValue(state.isPlaying)
        }
    }
    private fun setCorrectImageBasedOnIsPausePlayClickedValue(state:Boolean){
        if (state){
            binding.ivPlayPause.setImageResource(R.drawable.pause_svgrepo_com)
        }else{
            binding.ivPlayPause.setImageResource(R.drawable.play_svgrepo_com)
        }
    }
    override fun openPlayedSongBottomSheet(song: Song) {
        val bottomSheet = PlayedSongBottomSheet.newInstance(song)
        bottomSheet.show(parentFragmentManager,"playedSongBottomSheet")
    }
    companion object{
        const val SONGS = "Songs"
        const val ARTISTS = "Artists"
        const val PLAYLISTS = "Playlists"
    }
}