package com.example.musicapptraining.ui.fragments.playlistFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapptraining.data.model.PlayList
import com.example.musicapptraining.data.model.Song
import com.example.musicapptraining.data.repositories.PlayListRepository
import com.example.musicapptraining.utilities.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaylistViewModel @Inject constructor(
    private val playListRepository: PlayListRepository
) : ViewModel() {
    private var _playListsState : MutableStateFlow<UiState<List<PlayList>>> =
        MutableStateFlow(UiState.Loading)
    val playListsState = _playListsState.asStateFlow()
    private var _likedPlaylistState : MutableStateFlow<UiState<PlayList>> =
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
            val playLists = playListRepository.getPlayLists()
            playLists.collect{uiState->
                _playListsState.value = uiState
            }
        }
    }
    fun addNewPlayList(name : String){
        viewModelScope.launch {
            playListRepository.addNewPlayList(name)
        }
    }
    private fun getLikedPlaylist(){
        viewModelScope.launch {
           val likedPlaylist = playListRepository.getLikedPlaylist()
            likedPlaylist.collect{uiState->
                _likedPlaylistState.value = uiState
            }
        }
    }
    fun addSongToPlaylist(song: Song,playList: PlayList){
        viewModelScope.launch {
            _addSongToPlaylistUiState.value = UiState.Loading
            try {
                playListRepository.addSongToPlayList(song, playList)
                _addSongToPlaylistUiState.value = UiState.Success(Unit)
            }catch (e:Exception){
                _addSongToPlaylistUiState.value =
                    UiState.Error(e.message ?: "failed to add song")
            }

        }
    }
    fun deleteSongFromPlaylist(song: Song,playList: PlayList){
        viewModelScope.launch {
            _deleteSongFromPlaylistUiState.value = UiState.Loading
            try {
                playListRepository.deleteSongFromPlayList(song, playList)
                _deleteSongFromPlaylistUiState.value = UiState.Success(Unit)
            }catch (e:Exception){
                _deleteSongFromPlaylistUiState.value =
                    UiState.Error(e.message ?: "failed to remove song")
            }

        }
    }
}