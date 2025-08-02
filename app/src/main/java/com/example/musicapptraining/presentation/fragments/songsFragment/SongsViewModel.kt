package com.example.musicapptraining.presentation.fragments.songsFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapptraining.domain.model.Song
import com.example.musicapptraining.domain.usecases.songUseCases.GetAllSongsUseCase
import com.example.musicapptraining.utilities.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SongsViewModel @Inject constructor(
    private val getAllSongsUseCase: GetAllSongsUseCase
) : ViewModel() {
    private var _songListState : MutableStateFlow<UiState<List<Song>>>
    = MutableStateFlow(UiState.Loading)
    var songListState = _songListState.asStateFlow()
    fun fetchAllMusic(){
        viewModelScope.launch {
            _songListState.value = UiState.Loading
            val cachedAudio = getAllSongsUseCase()
            cachedAudio.catch {e->
                _songListState.value = UiState.Error("can't load audios:${e.message}")
            }.collect{ songs->
                _songListState.value = UiState.Success(songs)
            }
        }
    }
}