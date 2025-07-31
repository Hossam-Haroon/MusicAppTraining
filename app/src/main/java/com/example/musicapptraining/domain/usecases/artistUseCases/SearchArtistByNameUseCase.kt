package com.example.musicapptraining.domain.usecases.artistUseCases

import com.example.musicapptraining.domain.model.Artist
import com.example.musicapptraining.domain.repositories.ArtistRepository
import com.example.musicapptraining.utilities.UiState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchArtistByNameUseCase @Inject constructor(
    private val artistRepository: ArtistRepository
) {
    operator fun invoke(artistName:String): Flow<UiState<List<Artist>>>{
        return artistRepository.searchArtistByName(artistName)
    }
}