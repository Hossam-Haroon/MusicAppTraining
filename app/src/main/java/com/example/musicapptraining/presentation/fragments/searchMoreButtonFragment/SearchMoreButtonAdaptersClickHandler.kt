package com.example.musicapptraining.presentation.fragments.searchMoreButtonFragment

import androidx.navigation.NavController
import com.example.musicapptraining.presentation.fragments.artistFragment.ArtistAdapter
import com.example.musicapptraining.presentation.fragments.songsFragment.SongAdapter
import com.example.musicapptraining.presentation.musicPlayer.PlaybackViewModel
import com.example.musicapptraining.presentation.musicPlayer.PlayerViewModel
import com.example.musicapptraining.presentation.navigation.DefaultMediaNavigator
import com.example.musicapptraining.presentation.navigation.NavigableMedia
import com.example.musicapptraining.utilities.MoreButtonBottomSheetHandler
import com.example.musicapptraining.utilities.PlayedSongBottomSheetHandler
import com.example.musicapptraining.utilities.PlayerEvents

class SearchMoreButtonAdaptersClickHandler(
    private val songAdapter: SongAdapter,
    private val artistAdapter: ArtistAdapter,
    private val playerViewModel : PlayerViewModel,
    navController: NavController,
    private val playedSongBottomSheetHandler: PlayedSongBottomSheetHandler,
    private val moreButtonBottomSheetHandler: MoreButtonBottomSheetHandler
) {
    private val defaultMediaNavigator = DefaultMediaNavigator(navController)
    fun setAdaptersClickListeners(){
        setArtistAdapterClickListeners()
        setSongAdapterClickListeners()
    }
    private fun setSongAdapterClickListeners(){
        songAdapter.apply {
            setOnItemClickListener{song->
                playerViewModel.getEvent(
                    PlayerEvents.GetThePositionOfSpecificSongInsideThePlayList(song.songId)
                )
                playedSongBottomSheetHandler.openPlayedSongBottomSheet(song)
            }
            setOnMoreButtonClickListener { song->
                moreButtonBottomSheetHandler.openMoreButtonBottomSheet(song)
            }
        }
    }
    private fun setArtistAdapterClickListeners(){
        artistAdapter.setOnItemClickListener {
            defaultMediaNavigator.openSelectedMedia(
                NavigableMedia.ArtistMediaFromSearchMoreButtonFragment(it.artistName)
            )
        }
    }
}