package com.example.musicapptraining.ui.fragments.searchFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapptraining.domain.model.Artist
import com.example.musicapptraining.domain.model.Song
import com.example.musicapptraining.domain.usecases.artistUseCases.SearchArtistByNameUseCase
import com.example.musicapptraining.domain.usecases.songUseCases.SearchSongUseCase
import com.example.musicapptraining.utilities.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchSongUseCase: SearchSongUseCase,
    private val searchArtistByNameUseCase: SearchArtistByNameUseCase
) : ViewModel() {
    private var _songListState : MutableStateFlow<UiState<List<Song>>> =
        MutableStateFlow(UiState.Loading)
    val songListState  = _songListState.asStateFlow()
    private var _artistListState : MutableStateFlow<UiState<List<Artist>>> =
        MutableStateFlow(UiState.Loading)
    val artistListState = _artistListState.asStateFlow()
    fun getSearchedSongs(text: String){
        viewModelScope.launch {
            val songs = searchSongUseCase(text)
            songs.collect{uiState->
                _songListState.value = uiState
            }
        }
    }
    fun getSearchedArtists(text: String){
        viewModelScope.launch {
            val artists = searchArtistByNameUseCase(text)
            artists.collect{uiState->
                _artistListState.value = uiState
            }
        }
    }
    fun clearData(){
        _songListState.value = UiState.Loading
        _artistListState.value = UiState.Loading
    }
}