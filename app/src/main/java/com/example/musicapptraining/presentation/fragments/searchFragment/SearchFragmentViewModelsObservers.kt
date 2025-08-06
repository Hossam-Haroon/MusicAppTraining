package com.example.musicapptraining.presentation.fragments.searchFragment

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.musicapptraining.presentation.fragments.artistFragment.ArtistAdapter
import com.example.musicapptraining.presentation.fragments.songsFragment.SongAdapter
import com.example.musicapptraining.utilities.handleUiState
import kotlinx.coroutines.launch

class SearchFragmentViewModelsObservers(
    private val searchViewModel: SearchViewModel,
    private val songAdapter: SongAdapter,
    private val artistAdapter: ArtistAdapter,
    private val viewLifecycleCoroutineScope: LifecycleCoroutineScope,
    private val lifecycleOwner: LifecycleOwner
) {
    fun setViewmodelObservers(){
        viewLifecycleCoroutineScope.launch {
            lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                launch {
                    setSongViewmodelObserver()
                }
                launch {
                    setArtistViewmodelObserver()
                }
            }
        }
    }
    private suspend fun setSongViewmodelObserver(){
        searchViewModel.songListState.collect{uiState->
            with(songAdapter){
                handleUiState(
                    uiState = uiState,
                    successState = {songs->
                        val sortedList = songs.sortedByDescending { it.songDateAdded }
                        submitList(sortedList)
                    },
                    errorState = {
                        Log.d(
                            CHECK_SONGS_SEARCHING,
                            "Error: can't find the required song")
                    }
                )
            }
        }
    }
    private suspend fun setArtistViewmodelObserver(){
        searchViewModel.artistListState.collect{uiState->
            with(artistAdapter){
                handleUiState(
                    uiState = uiState,
                    successState = {artists->
                        val sortedList = artists.sortedByDescending { it.artistName }
                        submitList(sortedList)
                    },
                    errorState = {
                        Log.d(
                            CHECK_ARTIST_SEARCHING,
                            "Error: can't find the required artist")
                    }
                )
            }
        }
    }
    companion object{
        private const val CHECK_SONGS_SEARCHING = "check songs searching"
        private const val CHECK_ARTIST_SEARCHING = "check artist searching"
    }
}