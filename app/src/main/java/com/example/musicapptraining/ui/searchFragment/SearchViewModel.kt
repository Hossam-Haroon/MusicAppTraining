package com.example.musicapptraining.ui.searchFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapptraining.data.model.Album
import com.example.musicapptraining.data.model.Artist
import com.example.musicapptraining.data.model.Song
import com.example.musicapptraining.data.repositories.AlbumRepository
import com.example.musicapptraining.data.repositories.ArtistRepository
import com.example.musicapptraining.data.repositories.SongRepository
import com.example.musicapptraining.utilities.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SearchViewModel @Inject constructor(
    private val songRepository: SongRepository,
    private val artistsRepository: ArtistRepository
) : ViewModel() {
    private var _songListState : MutableStateFlow<UiState<List<Song>>> =
        MutableStateFlow(UiState.Loading)
    val songListState  = _songListState.asStateFlow()
    private var _artistListState : MutableStateFlow<UiState<List<Artist>>> =
        MutableStateFlow(UiState.Loading)
    val artistListState = _artistListState.asStateFlow()
    fun getSearchedSongs(text: String){
        viewModelScope.launch {
            val songs = songRepository.searchSong(text)
            songs.collect{uiState->
                _songListState.value = uiState
            }
        }
    }
    fun getSearchedArtists(text: String){
        viewModelScope.launch {
            val artists = artistsRepository.searchArtistByName(text)
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