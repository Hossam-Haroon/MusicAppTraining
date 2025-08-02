package com.example.musicapptraining.presentation.bottomSheetFragments.addToPlayListBottomSheet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapptraining.domain.model.Playlist
import com.example.musicapptraining.domain.model.Song
import com.example.musicapptraining.domain.usecases.playlistUseCases.AddSongToPlaylistUseCase
import com.example.musicapptraining.domain.usecases.playlistUseCases.GetAllPlaylistsUseCase
import com.example.musicapptraining.utilities.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AddToPlayListBottomSheetViewModel @Inject constructor(
    private val getAllPlaylistsUseCase: GetAllPlaylistsUseCase,
    private val addSongToPlaylistUseCase: AddSongToPlaylistUseCase
) : ViewModel() {
    private val _getPlayLists : MutableStateFlow<UiState<List<Playlist>>> =
        MutableStateFlow(UiState.Loading)
    val getPlayLists = _getPlayLists.asStateFlow()
    init {
        getPlayLists()
    }
    private fun getPlayLists(){
        viewModelScope.launch {
            _getPlayLists.value = UiState.Loading
            val playListsFlow = getAllPlaylistsUseCase()
            playListsFlow.catch { e->
                _getPlayLists.value = UiState.Error("can't load playlists: ${e.message}")
            }.collect{playlists ->
                _getPlayLists.value = UiState.Success(playlists)
            }
        }
    }
    fun addSongToPlayList(song:Song, playList: Playlist){
        viewModelScope.launch {
            addSongToPlaylistUseCase(song,playList)
        }
    }

}