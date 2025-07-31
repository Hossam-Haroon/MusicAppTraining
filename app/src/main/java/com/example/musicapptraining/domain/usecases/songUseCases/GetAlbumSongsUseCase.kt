package com.example.musicapptraining.domain.usecases.songUseCases

import com.example.musicapptraining.domain.model.Album
import com.example.musicapptraining.domain.repositories.SongRepository
import com.example.musicapptraining.utilities.UiState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAlbumSongsUseCase @Inject constructor(
    private val songRepository: SongRepository
) {
    operator fun invoke(albumName:String): Flow<UiState<Album>> {
        return songRepository.getAlbumSongs(albumName)
    }
}