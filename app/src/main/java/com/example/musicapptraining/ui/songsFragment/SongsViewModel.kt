package com.example.musicapptraining.ui.songsFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapptraining.data.model.Song
import com.example.musicapptraining.data.repositories.SongRepository
import com.example.musicapptraining.utilities.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SongsViewModel @Inject constructor(
    private val songRepository: SongRepository
) : ViewModel() {

    private var _songListState : MutableStateFlow<UiState<List<Song>>> = MutableStateFlow(UiState.Loading)

    var songListState = _songListState.asStateFlow()


    fun fetchAllMusic(){
        viewModelScope.launch {
            _songListState.value = UiState.Loading
            val cachedAudio = songRepository.getAllSongs()
            cachedAudio.collect{ resource->
                _songListState.value = resource
        }


        }
    }



}