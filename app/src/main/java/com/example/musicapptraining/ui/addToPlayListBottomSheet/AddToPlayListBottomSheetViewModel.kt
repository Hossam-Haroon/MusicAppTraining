package com.example.musicapptraining.ui.addToPlayListBottomSheet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapptraining.data.model.PlayList
import com.example.musicapptraining.data.model.Song
import com.example.musicapptraining.data.repositories.PlayListRepository
import com.example.musicapptraining.utilities.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AddToPlayListBottomSheetViewModel @Inject constructor(
   private val playListRepository: PlayListRepository
) : ViewModel() {

    val _getPlayLists : MutableStateFlow<UiState<List<PlayList>>> = MutableStateFlow(UiState.Loading)
    val getPlayLists = _getPlayLists.asStateFlow()

    fun getPlayLists(){
        viewModelScope.launch {
            _getPlayLists.value = UiState.Loading
            val playLists = playListRepository.getPlayLists()
            playLists.collect{uiState ->
                when(uiState){
                    is UiState.Error -> {}
                    UiState.Loading -> {}
                    is UiState.Success -> {
                        _getPlayLists.value = uiState
                    }
                }
            }
        }

    }

    fun addSongToPlayList(song:Song, playList: PlayList){
        viewModelScope.launch {
            playListRepository.addSongToPlayList(song,playList)
        }
    }

}