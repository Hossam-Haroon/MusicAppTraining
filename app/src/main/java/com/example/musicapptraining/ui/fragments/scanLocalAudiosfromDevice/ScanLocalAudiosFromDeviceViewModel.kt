package com.example.musicapptraining.ui.fragments.scanLocalAudiosfromDevice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapptraining.domain.model.Song
import com.example.musicapptraining.domain.usecases.songUseCases.CheckAndRefreshUseCase
import com.example.musicapptraining.utilities.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScanLocalAudiosFromDeviceViewModel @Inject constructor(
    private val checkAndRefreshUseCase: CheckAndRefreshUseCase
): ViewModel() {
    private var _audioListState: MutableStateFlow<UiState<List<Song>>> =
        MutableStateFlow(UiState.Loading)
     val audioListState = _audioListState.asStateFlow()
    init {
        checkAndRefresh()
    }
    fun checkAndRefresh(){
        viewModelScope.launch {
            _audioListState.value = UiState.Loading
            val checkedAudios = checkAndRefreshUseCase()
            _audioListState.value = checkedAudios
        }
    }
}