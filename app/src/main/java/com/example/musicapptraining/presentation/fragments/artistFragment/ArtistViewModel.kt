package com.example.musicapptraining.presentation.fragments.artistFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapptraining.domain.model.Artist
import com.example.musicapptraining.domain.usecases.artistUseCases.GetAllArtistsUseCase
import com.example.musicapptraining.utilities.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArtistViewModel @Inject constructor(
    private val getAllArtistsUseCase: GetAllArtistsUseCase
) : ViewModel() {
    private var _artistListState : MutableStateFlow<UiState<List<Artist>>> =
        MutableStateFlow(UiState.Loading)
    val artistListState = _artistListState.asStateFlow()
    init {
        getAllArtists()
    }
    private fun getAllArtists(){
        viewModelScope.launch {
            _artistListState.value = UiState.Loading
            val artistsFlow = getAllArtistsUseCase()
            artistsFlow.catch { e->
                _artistListState.value = UiState.Error("can't load artists: ${e.message}")
            }.collect{artists->
                _artistListState.value = UiState.Success(artists)
            }
        }
    }
}