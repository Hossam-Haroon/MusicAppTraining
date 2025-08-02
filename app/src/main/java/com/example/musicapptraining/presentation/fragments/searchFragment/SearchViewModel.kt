package com.example.musicapptraining.presentation.fragments.searchFragment

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
import kotlinx.coroutines.flow.catch
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
        _songListState.value = UiState.Loading
        viewModelScope.launch {
            val songsFlow = searchSongUseCase(text)
            songsFlow.catch {e->
                _songListState.value = UiState.Error("no such song exist:${e.message}")
            }.collect{songs->
                _songListState.value = UiState.Success(songs)
            }
        }
    }
    fun getSearchedArtists(text: String){
        viewModelScope.launch {
            _artistListState.value = UiState.Loading
            val searchedArtists = searchArtistByNameUseCase(text)
            searchedArtists.catch {e->
                _artistListState.value = UiState.Error("can't load searched artists: ${e.message}")
            }.collect{artists->
                _artistListState.value = UiState.Success(artists)
            }
        }
    }
    fun clearData(){
        _songListState.value = UiState.Loading
        _artistListState.value = UiState.Loading
    }
}