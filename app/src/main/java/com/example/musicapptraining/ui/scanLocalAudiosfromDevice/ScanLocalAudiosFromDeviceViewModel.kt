package com.example.musicapptraining.ui.scanLocalAudiosfromDevice

import android.util.Log
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
class ScanLocalAudiosFromDeviceViewModel @Inject constructor(
    private val musicRepository: SongRepository
): ViewModel() {

    private var _audioListState: MutableStateFlow<UiState<List<Song>>> = MutableStateFlow(UiState.Loading)
     val audioListState = _audioListState.asStateFlow()


    fun checkAndRefresh(){
        viewModelScope.launch {
            _audioListState.value = UiState.Loading
            val checkedAudios = musicRepository.checkAndRefresh()
            checkedAudios.collect{uiState->
                when(uiState){
                    is UiState.Error -> Log.d("error",uiState.message)
                    UiState.Loading -> {}
                    is UiState.Success -> {
                        _audioListState.value = uiState
                    }
                }

            }
        }

    }

}