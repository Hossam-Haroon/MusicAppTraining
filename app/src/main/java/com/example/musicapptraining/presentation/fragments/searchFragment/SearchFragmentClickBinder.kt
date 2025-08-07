package com.example.musicapptraining.presentation.fragments.searchFragment

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.navigation.NavController
import com.example.musicapptraining.databinding.FragmentSearchBinding
import com.example.musicapptraining.presentation.fragments.artistFragment.ArtistAdapter
import com.example.musicapptraining.presentation.fragments.songsFragment.SongAdapter
import com.example.musicapptraining.presentation.PlayerControllerViewModel.PlayerControllerViewModel
import com.example.musicapptraining.utilities.MoreButtonBottomSheetHandler
import com.example.musicapptraining.utilities.PlayedSongBottomSheetHandler
import com.example.musicapptraining.utilities.PlayerEvents

class SearchFragmentClickBinder(
    private val binding: FragmentSearchBinding,
    private val songAdapter:SongAdapter,
    private val artistAdapter: ArtistAdapter,
    private val playerViewModel: PlayerControllerViewModel,
    private val navController: NavController,
    private val searchViewModel: SearchViewModel,
    private val showPlayedSongBottomSheetHandler: PlayedSongBottomSheetHandler,
    private val showMoreButtonBottomSheetHandler: MoreButtonBottomSheetHandler
) {
    fun setupUIClickBinders(){
        setUiClickListeners()
        setSongAdapterClickListeners()
        setArtistAdapterClickListeners()
    }
    private fun setSongAdapterClickListeners(){
        songAdapter.apply {
            setOnItemClickListener{song->
                playerViewModel.getEvent(
                    PlayerEvents.GetThePositionOfSpecificSongInsideThePlayList(song.songId)
                )
                showPlayedSongBottomSheetHandler.openPlayedSongBottomSheet(song)
            }
            setOnMoreButtonClickListener { song->
                showMoreButtonBottomSheetHandler.openMoreButtonBottomSheet(song)
            }
        }
    }
    private fun setArtistAdapterClickListeners(){
        artistAdapter.setOnItemClickListener {
            val action = SearchFragmentDirections
                .actionSearchFragmentToArtistsAndAlbumsAndPlaylistsFragment(
                    artistName = it.artistName,
                    playListName = EMPTY_STRING,
                    albumName = EMPTY_STRING
                )
            navController.navigate(action)
        }
    }
    private fun setUiClickListeners(){
        binding.songMoreTv.setOnClickListener {
            try {
                val action = SearchFragmentDirections
                    .actionSearchFragmentToSearchMoreButtonFragment(
                        searchViewModel.songListState.value.toData()!!.toTypedArray(),
                        arrayOf(),
                        arrayOf()
                    )
                navController.navigate(action)
            }catch (e: Exception){
                Log.e(NAVIGATING_TO_SEE_MORE_SONGS, "Navigation error: ${e.message}")
            }

        }
        binding.artistMoreTv.setOnClickListener {
            try {
                val action = SearchFragmentDirections
                    .actionSearchFragmentToSearchMoreButtonFragment(
                        arrayOf(),
                        searchViewModel.artistListState.value.toData()!!.toTypedArray(),
                        arrayOf()
                    )
                navController.navigate(action)
            }catch (e: Exception){
                Log.e(NAVIGATING_TO_SEE_MORE_ARTISTS, "Navigation error: ${e.message}")
            }
        }
        binding.cancelButton.setOnClickListener {
            navController.navigateUp()
        }
        binding.searchEt.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(letter: CharSequence?, p1: Int, p2: Int, p3: Int) {
                letter?.let {
                    if (it.isNotEmpty()){
                        searchViewModel.getSearchedSongs(it.toString())
                        searchViewModel.getSearchedArtists(it.toString())
                    }else{
                        searchViewModel.clearData()
                    }
                }
            }
            override fun afterTextChanged(p0: Editable?) {
            }
        }
        )
    }
    companion object{
        private const val EMPTY_STRING = ""
        private const val NAVIGATING_TO_SEE_MORE_SONGS = "navigating to see more songs"
        private const val NAVIGATING_TO_SEE_MORE_ARTISTS = "navigating to see more artists"
    }
}