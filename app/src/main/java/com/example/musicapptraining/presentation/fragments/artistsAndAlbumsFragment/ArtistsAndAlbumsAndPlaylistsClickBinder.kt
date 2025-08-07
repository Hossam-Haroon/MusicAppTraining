package com.example.musicapptraining.presentation.fragments.artistsAndAlbumsFragment

import com.example.musicapptraining.databinding.FragmentArtistsAndAlbumsAndPlaylistsBinding
import com.example.musicapptraining.utilities.PlayedSongBottomSheetHandler
import com.example.musicapptraining.utilities.MoreButtonBottomSheetHandler
import com.example.musicapptraining.utilities.SortOptionBottomSheetHandler
import com.example.musicapptraining.presentation.fragments.songsFragment.SongAdapter
import com.example.musicapptraining.presentation.PlayerControllerViewModel.PlayerControllerViewModel
import com.example.musicapptraining.utilities.PlayerEvents

class ArtistsAndAlbumsAndPlaylistsClickBinder(
    private val playerViewmodel : PlayerControllerViewModel,
    private val songAdapter: SongAdapter,
    private val binding: FragmentArtistsAndAlbumsAndPlaylistsBinding,
    private val playedSongBottomSheetHandler: PlayedSongBottomSheetHandler,
    private val moreButtonBottomSheetHandler: MoreButtonBottomSheetHandler,
    private val sortOptionBottomSheetHandler: SortOptionBottomSheetHandler
){
    fun setupUIClicks(){
        setClickListeners()
        setAdapterCLickListeners()
    }
    private fun setClickListeners(){
        binding.apply {
            playAllTv.setOnClickListener {
                playerViewmodel.getEvent(PlayerEvents.GoToSpecificItem(0))
            }
            playAllImg.setOnClickListener {
                playerViewmodel.getEvent(PlayerEvents.GoToSpecificItem(0))
            }
            sortOptions.setOnClickListener {
                sortOptionBottomSheetHandler.openSortOptionBottomSheet()
            }
        }
    }
    private fun setAdapterCLickListeners(){
        songAdapter.apply {
            setOnItemClickListener { song ->
                playerViewmodel.getEvent(
                    PlayerEvents.GetThePositionOfSpecificSongInsideThePlayList(song.songId)
                )
                playedSongBottomSheetHandler.openPlayedSongBottomSheet(song)
            }
            setOnMoreButtonClickListener { song ->
                moreButtonBottomSheetHandler.openMoreButtonBottomSheet(song)
            }
        }
    }
}