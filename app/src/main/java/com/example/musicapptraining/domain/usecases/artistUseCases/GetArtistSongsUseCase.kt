package com.example.musicapptraining.domain.usecases.artistUseCases

import com.example.musicapptraining.domain.model.Artist
import com.example.musicapptraining.domain.repositories.ArtistRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetArtistSongsUseCase @Inject constructor(
    private val artistRepository: ArtistRepository
) {
    operator fun invoke(artistName:String): Flow<Artist> {
        return artistRepository.getArtistSongs(artistName)
    }
}