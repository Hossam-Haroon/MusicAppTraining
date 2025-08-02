package com.example.musicapptraining.presentation.fragments.artistsAndAlbumsFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapptraining.domain.model.Album
import com.example.musicapptraining.domain.model.Artist
import com.example.musicapptraining.domain.model.Playlist
import com.example.musicapptraining.domain.usecases.songUseCases.GetAlbumSongsUseCase
import com.example.musicapptraining.domain.usecases.songUseCases.GetArtistSongsUseCase
import com.example.musicapptraining.domain.usecases.songUseCases.GetPlaylistSongsUseCase
import com.example.musicapptraining.utilities.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArtistAndAlbumViewModel @Inject constructor(
   private val getArtistSongsUseCase: GetArtistSongsUseCase,
   private val getAlbumSongsUseCase: GetAlbumSongsUseCase,
   private val getPlaylistSongsUseCase: GetPlaylistSongsUseCase
): ViewModel() {
    private var _artistAudioList : MutableStateFlow<UiState<Artist>> =
        MutableStateFlow(UiState.Loading)
    val artistAudioList = _artistAudioList.asStateFlow()

    private var _albumAudioList : MutableStateFlow<UiState<Album>> =
        MutableStateFlow(UiState.Loading)
    val albumAudioList = _albumAudioList.asStateFlow()

    private var _playListAudioList : MutableStateFlow<UiState<Playlist>> =
        MutableStateFlow(UiState.Loading)
    val playListAudioList = _playListAudioList.asStateFlow()
    fun getArtistAudioList(artistName: String){
        viewModelScope.launch {
            _artistAudioList.value = UiState.Loading
           val artistFlow =  getArtistSongsUseCase(artistName)
            artistFlow.catch { e->
                _artistAudioList.value = UiState.Error("can't load artist songs:${e.message}")
            }.collect{artist->
                _artistAudioList.value = UiState.Success(artist)
            }
        }
    }
    fun getAlbumAudioList(albumName : String){
        viewModelScope.launch {
            _albumAudioList.value = UiState.Loading
            val albumFlow = getAlbumSongsUseCase(albumName)
            albumFlow.catch { e->
                _albumAudioList.value = UiState.Error("can't load album songs:${e.message}")
            }.collect{album->
                _albumAudioList.value = UiState.Success(album)
            }
        }
    }
    fun getPlaylistAudioList(playListName: String){
        viewModelScope.launch{
            _playListAudioList.value = UiState.Loading
            val playList = getPlaylistSongsUseCase(playListName)
            playList.catch { e->
                _playListAudioList.value = UiState.Error("can't load playlist songs:${e.message}")
            }.collect{playlist->
                _playListAudioList.value = if (playlist != null){
                    UiState.Success(playlist)
                }else{
                    UiState.Error("playlist is empty")
                }
            }
        }
    }
}