package com.example.musicapptraining.ui.playlistFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapptraining.data.model.PlayList
import com.example.musicapptraining.data.repositories.PlayListRepository
import com.example.musicapptraining.utilities.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaylistViewModel @Inject constructor(
    private val playListRepository: PlayListRepository
) : ViewModel() {

    private var _playListsState : MutableStateFlow<UiState<List<PlayList>>> = MutableStateFlow(UiState.Loading)

    val playListsState = _playListsState.asStateFlow()




    fun getAllPlayLists(){
        viewModelScope.launch {
            val playLists = playListRepository.getPlayLists()
            playLists.collect{uiState->
                when(uiState){
                    is UiState.Error -> {}
                    UiState.Loading -> {}
                    is UiState.Success -> {
                        _playListsState.value = uiState
                    }
                }

            }
        }
    }

    fun addNewPlayList(name : String){
        viewModelScope.launch {
            playListRepository.addNewPlayList(name)
            getAllPlayLists()
        }

    }

}