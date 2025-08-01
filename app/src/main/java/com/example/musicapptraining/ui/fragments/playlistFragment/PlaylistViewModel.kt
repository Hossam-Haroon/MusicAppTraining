package com.example.musicapptraining.ui.fragments.playlistFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapptraining.domain.model.Playlist
import com.example.musicapptraining.domain.model.Song
import com.example.musicapptraining.domain.usecases.playlistUseCases.AddNewPlaylistUseCase
import com.example.musicapptraining.domain.usecases.playlistUseCases.AddSongToPlaylistUseCase
import com.example.musicapptraining.domain.usecases.playlistUseCases.DeleteSongFromPlaylistUseCase
import com.example.musicapptraining.domain.usecases.playlistUseCases.GetAllPlaylistsUseCase
import com.example.musicapptraining.domain.usecases.playlistUseCases.GetLikedPlaylistUseCase
import com.example.musicapptraining.utilities.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaylistViewModel @Inject constructor(
    private val getAllPlaylistsUseCase: GetAllPlaylistsUseCase,
    private val addNewPlaylistUseCase: AddNewPlaylistUseCase,
    private val getLikedPlaylistUseCase: GetLikedPlaylistUseCase,
    private val addSongToPlaylistUseCase: AddSongToPlaylistUseCase,
    private val deleteSongFromPlaylistUseCase: DeleteSongFromPlaylistUseCase
) : ViewModel() {
    private var _playListsState : MutableStateFlow<UiState<List<Playlist>>> =
        MutableStateFlow(UiState.Loading)
    val playListsState = _playListsState.asStateFlow()
    private var _likedPlaylistState : MutableStateFlow<UiState<Playlist>> =
        MutableStateFlow(UiState.Loading)
    val likedPlaylistState = _likedPlaylistState.asStateFlow()
    private var _deleteSongFromPlaylistUiState : MutableStateFlow<UiState<Unit>> =
        MutableStateFlow(UiState.Loading)
    val deleteSongFromPlaylistUiState = _deleteSongFromPlaylistUiState.asStateFlow()
    private var _addSongToPlaylistUiState : MutableStateFlow<UiState<Unit>> =
        MutableStateFlow(UiState.Loading)
    val addSongToPlaylistUiState = _addSongToPlaylistUiState.asStateFlow()
    init {
        getAllPlayLists()
        getLikedPlaylist()
    }
    private fun getAllPlayLists(){
        viewModelScope.launch {
            val playLists = getAllPlaylistsUseCase()
            playLists.collect{uiState->
                _playListsState.value = uiState
            }
        }
    }
    fun addNewPlayList(name : String){
        viewModelScope.launch {
            addNewPlaylistUseCase(name)
        }
    }
    private fun getLikedPlaylist(){
        viewModelScope.launch {
           val likedPlaylist = getLikedPlaylistUseCase()
            likedPlaylist.collect{uiState->
                _likedPlaylistState.value = uiState
            }
        }
    }
    fun addSongToPlaylist(song: Song, playList: Playlist){
        viewModelScope.launch {
            _addSongToPlaylistUiState.value = UiState.Loading
            try {
                addSongToPlaylistUseCase(song,playList)
                _addSongToPlaylistUiState.value = UiState.Success(Unit)
            }catch (e:Exception){
                _addSongToPlaylistUiState.value =
                    UiState.Error(e.message ?: "failed to add song")
            }
        }
    }
    fun deleteSongFromPlaylist(song: Song,playList: Playlist){
        viewModelScope.launch {
            _deleteSongFromPlaylistUiState.value = UiState.Loading
            try {
                deleteSongFromPlaylistUseCase(song,playList)
                _deleteSongFromPlaylistUiState.value = UiState.Success(Unit)
            }catch (e:Exception){
                _deleteSongFromPlaylistUiState.value =
                    UiState.Error(e.message ?: "failed to remove song")
            }
        }
    }
}