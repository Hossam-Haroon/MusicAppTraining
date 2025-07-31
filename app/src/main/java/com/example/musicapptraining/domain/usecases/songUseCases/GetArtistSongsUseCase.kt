package com.example.musicapptraining.domain.usecases.songUseCases

import com.example.musicapptraining.domain.model.Artist
import com.example.musicapptraining.domain.repositories.SongRepository
import com.example.musicapptraining.utilities.UiState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetArtistSongsUseCase @Inject constructor(
    private val songRepository: SongRepository
) {
    operator fun invoke(artistName:String): Flow<UiState<Artist>> {
        return songRepository.getArtistSongs(artistName)
    }
}