package com.example.musicapptraining.presentation.fragments.songsFragment

import androidx.lifecycle.LifecycleCoroutineScope
import com.example.musicapptraining.databinding.FragmentSongsBinding
import com.example.musicapptraining.presentation.playerControllerViewModel.PlayerControllerViewModel
import com.example.musicapptraining.utilities.MoreButtonBottomSheetHandler
import com.example.musicapptraining.utilities.PlayedSongBottomSheetHandler
import com.example.musicapptraining.utilities.PlayerEvents
import com.example.musicapptraining.utilities.SortOptionBottomSheetHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SongsFragmentClickBinder(
    private val playerViewModel:PlayerControllerViewModel,
    private val songAdapter: SongAdapter,
    private val binding: FragmentSongsBinding,
    private val showPlayedSongBottomSheetHandler: PlayedSongBottomSheetHandler,
    private val showMoreButtonBottomSheetHandler: MoreButtonBottomSheetHandler,
    private val showSortOptionBottomSheetHandler: SortOptionBottomSheetHandler,
    private val viewLifecycleCoroutineScope: LifecycleCoroutineScope
) {
    fun setupUIClicks(){
        setCLickListeners()
        setAdapterClickListeners()
    }
    private fun setAdapterClickListeners(){
        songAdapter.apply {
            setOnItemClickListener{song->
                playerViewModel.reconnectIfNeededUseCase()
                viewLifecycleCoroutineScope.launch {
                    delay(300)
                    playerViewModel.getEvent(
                        PlayerEvents.GetThePositionOfSpecificSongInsideThePlayList(song.songId)
                    )
                    showPlayedSongBottomSheetHandler.openPlayedSongBottomSheet(song)
                }
            }
            setOnMoreButtonClickListener { song->
                showMoreButtonBottomSheetHandler.openMoreButtonBottomSheet(song)
            }
        }
    }
    private fun setCLickListeners(){
        binding.apply{
            playAllTv.setOnClickListener {
                playerViewModel.getEvent(PlayerEvents.GoToSpecificItem(0))
            }
            playAllImg.setOnClickListener {
                playerViewModel.getEvent(PlayerEvents.GoToSpecificItem(0))
            }
            sortOptions.setOnClickListener {
                showSortOptionBottomSheetHandler.openSortOptionBottomSheet()
            }
        }
    }
}