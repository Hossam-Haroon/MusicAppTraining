package com.example.musicapptraining.ui.albumFragment

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapptraining.data.model.Album
import com.example.musicapptraining.data.repositories.AlbumRepository
import com.example.musicapptraining.utilities.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AlbumViewModel @Inject constructor(
    private val albumRepository: AlbumRepository
) : ViewModel() {

    private var _albumsListState : MutableStateFlow<UiState<List<Album>>> = MutableStateFlow(UiState.Loading)
    val albumsListState = _albumsListState.asStateFlow()


    fun getAllAlbums(){
        viewModelScope.launch {
            _albumsListState.value = UiState.Loading
           val albums =  albumRepository.getAllAlbums()
            albums.collect{uiState->
                when(uiState){
                    is UiState.Error -> Log.d("error",uiState.message)
                    UiState.Loading -> {}
                    is UiState.Success -> {
                        _albumsListState.value = uiState
                    }
                }

            }
        }
    }

}