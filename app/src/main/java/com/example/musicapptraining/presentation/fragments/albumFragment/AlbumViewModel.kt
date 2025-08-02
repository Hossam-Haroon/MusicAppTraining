package com.example.musicapptraining.presentation.fragments.albumFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapptraining.domain.model.Album
import com.example.musicapptraining.domain.usecases.albumUseCases.GetAllAlbumsUseCase
import com.example.musicapptraining.utilities.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AlbumViewModel @Inject constructor(
    private val getAllAlbumsUseCase: GetAllAlbumsUseCase
) : ViewModel() {
    private var _albumsListState : MutableStateFlow<UiState<List<Album>>> =
        MutableStateFlow(UiState.Loading)
    val albumsListState = _albumsListState.asStateFlow()
    fun getAllAlbums(){
        viewModelScope.launch {
            _albumsListState.value = UiState.Loading
           val albums = getAllAlbumsUseCase()
            try {
                albums.collect{uiState->
                    _albumsListState.value = UiState.Success(uiState)
                }
            }catch (e:Exception){
                _albumsListState.value = UiState.Error("Failed to load albums: ${e.message}")
            }
        }
    }
}