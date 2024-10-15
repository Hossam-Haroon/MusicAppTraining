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
    private val artistsRepository: ArtistRepository,
    private val albumRepository: AlbumRepository
) : ViewModel() {
    private var _songListState : MutableStateFlow<UiState<List<Song>>> = MutableStateFlow(UiState.Loading)
    val songListState  = _songListState.asStateFlow()
    private var _artistListState : MutableStateFlow<UiState<List<Artist>>> = MutableStateFlow(UiState.Loading)
    val artistListState = _artistListState.asStateFlow()
    private var _albumListState : MutableStateFlow<UiState<List<Album>>> = MutableStateFlow(UiState.Loading)
    val albumListState = _albumListState.asStateFlow()


    fun getSearchedSongs(text: String){
        viewModelScope.launch {
            _songListState.value = UiState.Loading
            val songs = songRepository.searchSong(text)
            songs.collect{uiState->
                when(uiState){
                    is UiState.Error -> {}
                    UiState.Loading -> {}
                    is UiState.Success -> {
                        _songListState.value = uiState
                    }
                }

            }
        }
    }
    fun getSearchedArtists(text: String){
        viewModelScope.launch {
            _artistListState.value = UiState.Loading
            val artists = artistsRepository.searchArtistByName(text)
            artists.collect{uiState->
                when(uiState){
                    is UiState.Error -> {}
                    UiState.Loading -> {}
                    is UiState.Success -> {
                        _artistListState.value = uiState
                    }
                }

            }
        }
    }
    fun getSearchedAlbums(text: String){
        viewModelScope.launch {
            _albumListState.value = UiState.Loading
            val albums = albumRepository.searchAlbumByName(text)
            albums.collect{uiState->
                when(uiState){
                    is UiState.Error -> {}
                    UiState.Loading -> {}
                    is UiState.Success -> {
                        _albumListState.value = uiState
                    }
                }

            }
        }
    }
    fun clearData(){
        _songListState.value = UiState.Loading
        _artistListState.value = UiState.Loading
        _albumListState.value = UiState.Loading
    }

}