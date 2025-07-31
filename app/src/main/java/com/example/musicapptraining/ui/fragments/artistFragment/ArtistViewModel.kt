package com.example.musicapptraining.ui.fragments.artistFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapptraining.domain.model.Artist
import com.example.musicapptraining.domain.usecases.artistUseCases.GetAllArtistsUseCase
import com.example.musicapptraining.utilities.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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
            val artists = getAllArtistsUseCase()
            artists.collect{uiState->
                _artistListState.value = uiState
            }
        }
    }
}