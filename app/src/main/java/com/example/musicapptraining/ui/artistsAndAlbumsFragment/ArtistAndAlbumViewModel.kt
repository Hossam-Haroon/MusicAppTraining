package com.example.musicapptraining.ui.artistsAndAlbumsFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapptraining.data.model.Album
import com.example.musicapptraining.data.model.Artist
import com.example.musicapptraining.data.model.PlayList
import com.example.musicapptraining.data.repositories.SongRepository
import com.example.musicapptraining.utilities.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArtistAndAlbumViewModel @Inject constructor(
   private val songRepository: SongRepository
): ViewModel() {

    private var _artistAudioList : MutableStateFlow<UiState<Artist>> = MutableStateFlow(UiState.Loading)
    val artistAudioList = _artistAudioList.asStateFlow()

    private var _albumAudioList : MutableStateFlow<UiState<Album>> = MutableStateFlow(UiState.Loading)
    val albumAudioList = _albumAudioList.asStateFlow()

    private var _playListAudioList : MutableStateFlow<UiState<PlayList>> = MutableStateFlow(UiState.Loading)
    val playListAudioList = _playListAudioList.asStateFlow()

    fun getArtistAudioList(artistName: String){
        viewModelScope.launch {
            _artistAudioList.value = UiState.Loading
           val artist =  songRepository.getArtistSongs(artistName)
            artist.collect{resource->
                _artistAudioList.value = resource
            }
        }
    }

    fun getAlbumAudioList(albumName : String){
        viewModelScope.launch {
            _albumAudioList.value = UiState.Loading
            val album = songRepository.getAlbumSongs(albumName)
            album.collect{resource->
                _albumAudioList.value = resource

            }
        }
    }

    fun getPlaylistAudioList(playListName: String){
        viewModelScope.launch{
            _playListAudioList.value = UiState.Loading
            val playList = songRepository.getPlaylistSongs(playListName)
            playList.collect{resource->
                _playListAudioList.value = resource

            }
        }


    }


}