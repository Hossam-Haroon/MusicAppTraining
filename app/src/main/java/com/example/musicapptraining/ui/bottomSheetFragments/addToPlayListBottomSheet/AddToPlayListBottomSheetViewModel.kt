package com.example.musicapptraining.ui.bottomSheetFragments.addToPlayListBottomSheet

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
    private val _getPlayLists : MutableStateFlow<UiState<List<PlayList>>> =
        MutableStateFlow(UiState.Loading)
    val getPlayLists = _getPlayLists.asStateFlow()
    init {
        getPlayLists()
    }
    private fun getPlayLists(){
        viewModelScope.launch {
            val playLists = playListRepository.getPlayLists()
            playLists.collect{uiState ->
                _getPlayLists.value = uiState
            }
        }
    }
    fun addSongToPlayList(song:Song, playList: PlayList){
        viewModelScope.launch {
            playListRepository.addSongToPlayList(song,playList)
        }
    }

}